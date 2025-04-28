import os
import socket
import socketserver
import scipy.io.wavfile as wav
import wave
import numpy as np
import pandas as pd


sub_id = "000-1"
timestamp = "-"

fileFreq = 48000
sub_id = "9999"

class MyTCPHandler(socketserver.BaseRequestHandler):

    def handle(self):
        global sub_id
        global timestamp

        total = 0
        chunks = []

        while True:
            self.data = self.request.recv(8192)
            total += len(self.data)
            if not self.data:
                break
            else:
                chunks.append(self.data)

        fullPacket = b''.join(chunks)
        print("Recd", total, len(chunks), len(fullPacket),
              "bytes from", self.client_address[0])
        header = fullPacket[0:15].decode("utf-8")
        print(header)

        if (header[0:5] == "SUBID"):
            sub_id = header[6:10]
            timestamp = fullPacket[10:].decode("utf-8")
            sub_dir = "sub" + sub_id + "_" + timestamp
            try:
                os.mkdir(sub_dir)
                os.chdir(sub_dir)
                print("Working directory set as %s\n" % os.getcwd())
                file_object = open(sub_id + "_" + timestamp + ".csv", 'a')
                file_object.write("subNum,blockNum,trialNum,pose,trialStartTime,trialEndTime,Acce,Gyro,Magnet\n")

                file_object.close()
                print("Fileheader writen")
                # NOT FILE WRITE YET
            except:
                print("Exception: working directory remains %s\n" % os.getcwd())
        
        elif (header[0:5] == "BLOCK"):
            try:
                file_object = open(sub_id + "_" + timestamp + ".csv", 'a')
                txt = fullPacket[6:].decode("utf-8")
                file_object.write(txt + "\n")
                file_object.close()
            except:
                print("Data file write failed. ERROR!")

        elif (header[0:5] == "SOUND"):
            if (timestamp == "-"):  # not yet started
                print("Test sound recevied: connected.")
            else:
                trial_id = header[7:10]
                fullPacket = fullPacket[10:]
                print('Saving: trial' + trial_id + '.wav')
                # we have two bytes/sample and two channels, so four bytes for each time
                sz = int(len(fullPacket)/2)
                soundFile = np.empty([sz, 1], dtype=np.int16)
                for index in range(0, sz):
                    i = index * 2
                    value = (fullPacket[i+1] << 8) + fullPacket[i]    # little endian
                    if value > 32767:  # If value exceeds max int16
                        value -= 65536
                    value = np.int16(value)
                    soundFile[index][0] = value

                wav.write('audio' + trial_id + '.wav', int(fileFreq), soundFile)


if __name__ == "__main__":
    path = os.getcwd()
    host_ip = 'localhost'
    host_port = 50005

    print("\n-------------\n")
    print("The current working directory is %s\n" % path)
    print("Starting up a TCP server - ")

    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        host_ip = (s.getsockname()[0])
        print("IP : ", host_ip)
    except:
        print("Unable to get Hostname and IP")

    server_address = (host_ip, host_port)



    # Create the server, binding to localhost on port 9999
    with socketserver.TCPServer(server_address, MyTCPHandler) as server:
        # Activate the server; this will keep running until you
        # interrupt the program with Ctrl-C
        server.serve_forever()
