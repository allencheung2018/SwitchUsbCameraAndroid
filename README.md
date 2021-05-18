1. It will scan all the USB cameras connecting the device with the library jiangdongguo:AndroidUSBCamera.

You can switch the camera:
public void onAttachDev(UsbDevice device) {
            Log.d("onAttachDev", "device:"+device.getDeviceName());
            if (!isRequest) {
                isRequest = true;
                if (mCameraHelper != null) {
                    List<UsbDevice> list = mCameraHelper.getUsbDeviceList();
                    mCameraHelper.requestPermission(0);
                }
            }
        }

2. Imageview draw grids and line when camera previewing.

3. Remember set the formats size supported by the specific camera.