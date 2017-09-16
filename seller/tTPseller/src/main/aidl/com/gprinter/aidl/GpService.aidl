package com.gprinter.aidl;
interface GpService{  
	int openPort(int printerId,int portType,String deviceName,int portNumber);
	void closePort(int printerId);
	int getPrinterConnectStatus(int printerId);
	int printeTestPage(int printerId);
  	int queryPrinterStatus(int printerId,int timeOut);
  	int getPrinterCommandType(int printerId);
	int sendEscCommand(int printerId, String base64);
  	int sendTscCommand(int printerId, String  base64);
}       