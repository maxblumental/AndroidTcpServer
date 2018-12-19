# Usage

1) Forward host port to device port:
```
$ adb forward tcp:<host_port> tcp:<device_port>
```
Ensure that the forwarding is active:
```
$ adb forward --list
```
2) Start the server on the device.

3) Run the client script:
```
$ cd app/src/
$ python tcp_client.py
```