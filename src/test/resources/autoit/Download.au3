;If you wait long enough then IE9 will automatically give focus to the Save As popup box
Sleep(4000)

Local $hIE = WinGetHandle("[Class:IEFrame]")
Local $hCtrl = ControlGetHandle($hIE, "", "[ClassNN:DirectUIHWND1]")
ControlSend($hIE ,"",$hCtrl,"{TAB}")          ; Gives focus to Open Button
Sleep(500)
ControlSend($hIE ,"",$hCtrl,"{TAB}")          ; Gives focus to Save Button
Sleep(500)
ControlSend($hIE ,"",$hCtrl,"{enter}")        ; Submit whatever control has focus

;Give IE enough time to complete the download before proceeding
Sleep(4000)