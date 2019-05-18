%====================================================================================
% Context ctxReqConsole  SYSTEM-configuration: file it.unibo.ctxReqConsole.finalSys2018Req.pl 
%====================================================================================
context(ctxreqconsole, "localhost",  "TCP", "8020" ).  		 
context(ctxreqrobot, "localhost",  "TCP", "8021" ).  		 
context(ctxreqsensorsactuators, "localhost",  "TCP", "8022" ).  		 
%%% -------------------------------------------
qactor( reqconsole , ctxreqconsole, "it.unibo.reqconsole.MsgHandle_Reqconsole"   ). %%store msgs 
qactor( reqconsole_ctrl , ctxreqconsole, "it.unibo.reqconsole.Reqconsole"   ). %%control-driven 
qactor( reqrobot , ctxreqrobot, "it.unibo.reqrobot.MsgHandle_Reqrobot"   ). %%store msgs 
qactor( reqrobot_ctrl , ctxreqrobot, "it.unibo.reqrobot.Reqrobot"   ). %%control-driven 
qactor( reqtherm , ctxreqsensorsactuators, "it.unibo.reqtherm.MsgHandle_Reqtherm"   ). %%store msgs 
qactor( reqtherm_ctrl , ctxreqsensorsactuators, "it.unibo.reqtherm.Reqtherm"   ). %%control-driven 
qactor( reqclock , ctxreqsensorsactuators, "it.unibo.reqclock.MsgHandle_Reqclock"   ). %%store msgs 
qactor( reqclock_ctrl , ctxreqsensorsactuators, "it.unibo.reqclock.Reqclock"   ). %%control-driven 
qactor( reqlamp , ctxreqsensorsactuators, "it.unibo.reqlamp.MsgHandle_Reqlamp"   ). %%store msgs 
qactor( reqlamp_ctrl , ctxreqsensorsactuators, "it.unibo.reqlamp.Reqlamp"   ). %%control-driven 
%%% -------------------------------------------
%%% -------------------------------------------

