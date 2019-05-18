%====================================================================================
% Context ctxFinalSysTesting  SYSTEM-configuration: file it.unibo.ctxFinalSysTesting.finalSys2018testing.pl 
%====================================================================================
context(ctxfinalsystesting, "localhost",  "TCP", "8011" ).  		 
%%% -------------------------------------------
qactor( cleaner , ctxfinalsystesting, "it.unibo.cleaner.MsgHandle_Cleaner"   ). %%store msgs 
qactor( cleaner_ctrl , ctxfinalsystesting, "it.unibo.cleaner.Cleaner"   ). %%control-driven 
%%% -------------------------------------------
%%% -------------------------------------------

