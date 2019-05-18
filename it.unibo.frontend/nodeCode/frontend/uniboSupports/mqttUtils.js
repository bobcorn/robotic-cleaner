/*
* =====================================
* uniboSupports/mqttUtils.js
* =====================================
*/
const mqtt   = require ('mqtt');
const topic  = "unibo/qasys";
//var client = mqtt.connect('mqtt://iot.eclipse.org');
var client   = mqtt.connect('mqtt://localhost');
//var client   = mqtt.connect('tcp://192.168.43.229:1883');

console.log("mqtt client= " + client );

client.on('connect', function () {
	  client.subscribe( topic );
	  console.log('client has subscribed successfully ');
});

//The message usually arrives as buffer, so I had to convert it to string data type;
client.on('message', function (topic, message){
  console.log("mqtt RECEIVES:"+ message.toString()); //if toString is not given, the message comes as buffer
});

exports.publish = function( msg ){
	//console.log('mqtt publish ' + client);
	client.publish(topic, msg);
}