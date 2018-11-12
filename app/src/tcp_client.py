import socket

if __name__ == '__main__':
    sock = socket.socket()
    sock.connect(('localhost', 9123))
    sock.send('hello, world!\n'.encode())

    data = sock.recv(1024)
    sock.send('bye\n'.encode())
    sock.close()

    print(data)
