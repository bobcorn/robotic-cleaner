/*
 * it.unibo.frontend/nodeCode/frontend/applCodeRobot.js
 */
var express     	= require('express');
var path         	= require('path');
var favicon      	= require('serve-favicon');
var logger       	= require('morgan');	// see 10.1 of nodeExpressWeb.pdf;
var cookieParser 	= require('cookie-parser');
var bodyParser   	= require('body-parser');
var fs           	= require('fs');
var toRobot;		// To be set if external actuator is set
var serverWithSocket= require('./robotFrontendServer');
var cors            = require('cors');
var robotModel      = require('./appServer/models/robot');
var User            = require("./appServer/models/user");
var mqttUtils;		// To be set later;
var session;		// To be set later for AUTH;
var passport;		// To be set later for AUTH;
var setUpPassport;	// To be set later for AUTH;
var mongoose;		// To be set later for AUTH;
var flash;			// To be set later for AUTH;

var app              = express();

var externalActuator = true; // When true, the application logic is external to the server;
var withAuth         = true;
var debugging		 = false;	

// view engine setup;
app.set('views', path.join(__dirname, 'appServer', 'viewRobot'));	 
app.set("view engine", "ejs");

// create a write stream (in append mode) ;
var accessLogStream = fs.createWriteStream(path.join(__dirname, 'morganLog.log'), {flags: 'a'})
app.use(logger("short", {stream: accessLogStream}));

app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev')); // shows commands, e.g. GET /pi 304 23.123 ms - -;
app.use(bodyParser.json());
app.use( cors() );  // npm install cors --save ;
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());

app.use(express.static(path.join(__dirname, 'jsCode')))

if( externalActuator )
	mqttUtils	= require('./uniboSupports/mqttUtils');
else
	toRobot		= require("./jsCode/clientRobotVirtual");

if(withAuth){
	session          = require("express-session");	 
	passport         = require("passport");			 
	setUpPassport    = require("./setuppassport");   
	mongoose         = require("mongoose");			 
	flash            = require("connect-flash");     	
	
	setUpAuth();
}

app.use(function(req, res, next) {
	res.locals.debugging = debugging;
	if(!withAuth)
		res.locals.currentUser = {name: function() { return "anonymous"}};
	next();
});

/*
 * ====================== AUTH ================
 */	
app.get('/', function(req, res) {
 	if( withAuth ) res.render("login");
 	else res.render("access");
 });	

 app.get("/login", function(req, res) {
 	res.render("login");
 });
 if(passport)
 	app.post("/login", passport.authenticate("login", {
 		successRedirect: "/access",			 
 		failureRedirect: "/login",
 		failureFlash: true
 	}));

 app.get("/access", ensureAuthenticated, function(req, res, next) {	 
		// res.send("wait a moment ...");
		res.render("access");		 
	});
 app.get("/logout", function(req, res) {
 	req.logout();
 	res.redirect("/");
 });
 app.get("/signup", function(req, res) {
 	res.render("signup");
 });

 if(passport)
 	app.post("/signup", function(req, res, next) {
 		var username = req.body.username;
 		var password = req.body.password;

 		User.findOne({ username: username }, function(err, user) {

 			if (err) { return next(err); }
 			if (user) {
 				req.flash("error", "User already exists");
 				return res.redirect("/signup");
 			}

 			var newUser = new User({
 				username: username,
 				password: password
 			});
 			newUser.save(next);

 		});
 	}, passport.authenticate("login", {
 		successRedirect: "/",
 		failureRedirect: "/signup",
 		failureFlash: true
 	}));

 app.get("/users/:username", function(req, res, next) {
 	User.findOne({ username: req.params.username }, function(err, user) {
 		if (err) { return next(err); }
 		if (!user) { return next(404); }
 		res.render("profile", { user: user });
 	});
 });

 app.get("/edit", ensureAuthenticated, function(req, res) {
 	res.render("edit");
 });

 app.post("/edit", ensureAuthenticated, function(req, res, next) {
 	req.user.displayName = req.body.displayname;
 	req.user.bio = req.body.bio;
 	req.user.save(function(err) {
 		if (err) {
 			next(err);
 			return;
 		}
 		req.flash("info", "Profile updated!");
 		res.redirect("/edit");
 	});
 });

/*
 * ====================== COMMANDS ================
 */
app.post("/robot/actions/commands/appl/go", function(req, res) {
	console.info("START THE APPLICATION");
	if( externalActuator ) delegate( "x(go)", "application", req, res);
});

app.post("/robot/actions/commands/appl/halt", function(req, res) {
	console.info("STOP THE APPLICATION");
	if( externalActuator ) delegate( "x(halt)", "application", req, res);
});	

app.post("/robot/actions/commands/w", function(req, res) {
	if( externalActuator ) delegate( "w(0)", "moving forward", req, res);
	else actuate( `{ "type": "moveForward",  "arg": -1 }`, "server moving forward", req, res);
});	
app.post("/robot/actions/commands/s", function(req, res) {
	if( externalActuator ) delegate( "s(0)", "moving backward", req, res );
	else actuate( `{ "type": "moveBackward",  "arg": -1 }`, "server moving backward", req, res);
});	
app.post("/robot/actions/commands/a", function(req, res) {
	if( externalActuator ) delegate( "a", "moving left", req, res );
	else actuate( `{ "type": "turnLeft",  "arg": 1000 }`, "server moving left", req, res);
});	
app.post("/robot/actions/commands/d", function(req, res) {
	if( externalActuator ) delegate( "d", "moving right", req, res );
	else actuate( `{ "type": "turnRight",  "arg": 1000 }`, "server moving right", req, res);
});	
app.post("/robot/actions/commands/h", function(req, res) {
	if( externalActuator ) delegate( "h", "stopped", req, res );
	else actuate( `{  "type": "alarm",  "arg": 1000 }`, "server stopped", req, res);
});		

	/*
	 * ====================== REPRESENTATION ================
	 */
app.use( function(req,res){
	console.info("SENDING THE ANSWER " + req.result  );
	try{
		if( req.result != undefined)
			serverWithSocket.updateClient( JSON.stringify(req.result ) );
		res.send(req.result);
	} catch(e){console.info("SORRY ...");}
}
);
// catch 404 and forward to error handler;
app.use(function(req, res, next) {
	var err = new Error('Not Found');
	err.status = 404;
	next(err);
});

// error handler;
app.use(function(err, req, res, next) {
	// set locals, only providing error in development
	res.locals.message = err.message;
	res.locals.error = req.app.get('env') === 'development' ? err : {};

	// render the error page;
	res.status(err.status || 500);
	res.render('error');
});

// =================== UTILITIES =========================

function setUpAuth(){ // AUTH
	try{	
		console.log("\tWORKING WITH AUTH ... "  ) ;
		mongoose.connect("mongodb://localhost:27017/test");
		setUpPassport();	
		app.use(session({	 
			secret: "LUp$Dg?,I#i&owP3=9su+OB%`JgL4muLF5YJ~{;t",
			resave: true,
			saveUninitialized: true
		}));
		app.use(flash());
		app.use(passport.initialize());
		app.use(passport.session());
		app.use(function(req, res, next) {
			res.locals.currentUser = req.user;
			res.locals.errors      = req.flash("error");
			res.locals.infos       = req.flash("info");
			next();
		});
	} catch( e ){
		console.log("SORRY, ERROR ... " + e) ;
	}	
}

function ensureAuthenticated(req, res, next) {
	if (req.isAuthenticated()) {
		next();
	} else {
		req.flash("info", "You must be logged in to see this page.");
		res.redirect("/login");
	}
}

function delegate( hlcmd, newState, req, res ){
	robotModel.robot.state = newState;
	emitRobotCmd(hlcmd);
	res.render("access");
}
function actuate(cmd, newState, req, res ){
	toRobot.send( cmd );
	robotModel.robot.state = newState;
	res.render("access");
}
var emitRobotCmd = function( cmd ){ // called by delegate;
	var eventstr = "msg(usercmd,event,js,none,usercmd(robotgui( " +cmd + ")),1)"
	console.log("emits> "+ eventstr);
	mqttUtils.publish( eventstr );	// topic = "unibo/qasys";
}

module.exports = app;
