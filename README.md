# Data Collection Using Samsung Galaxy Watch6

## About
This is an Android Studio Project that can capture and transmit audio and IMU data to a TCP server.

Utilities.java file provides general hyperparmeters needed to be adjusted.  
It includes the number of block repetitions, trial repetitions, and postures.   
You also can adjust the recording time of each trial and toggle IMU logging.   

## Before you run the program
Please run the Python TCP server first on your terminal.  
```
python3 tcpserver.py
```

Then, set your IP address to the denoted IP address on the terminal.   
Then, you can set the participant ID. (default 0).   

Check whether you can capture the data correctly.   
Please let me know if you need any update or help for using this system.   

## After you run the program
You may need to convert wav file to raw file (not necessarily) to train data.   

if audios are saved into 'sub0003_0221_234403' folder and they are named as audio_000 ... audio 010, and if you want to save them into 'test' folder, use this code.  

```
python3 covert_wav2raw.py -i 0 -e 10 -s sub0003_0221_234403 -d test                     
```