import wave
import argparse
import os

def wav_to_raw(input_wav, output_raw):
    """Converts a .wav file to a .raw file."""
    with wave.open(input_wav, 'rb') as wav_file:
        frames = wav_file.readframes(wav_file.getnframes())

    with open(output_raw, 'wb') as raw_file:
        raw_file.write(frames)

def batch_convert(start_idx, end_idx, data_path, save_path):
    """Converts multiple .wav files from data_path and saves them in save_path."""
    os.makedirs(save_path, exist_ok=True)  # Ensure the save path exists

    for i in range(start_idx, end_idx + 1):
        input_wav_file = os.path.join(data_path, f"audio{i:03d}.wav")  # audio000.wav, audio001.wav, ...
        output_raw_file = os.path.join(save_path, f"audio{i+1:03d}.raw")

        if os.path.exists(input_wav_file):
            print(f"Converting {input_wav_file} -> {output_raw_file}")
            wav_to_raw(input_wav_file, output_raw_file)
        else:
            print(f"Warning: {input_wav_file} not found, skipping.")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Convert WAV files to RAW format.")
    parser.add_argument("-i", "--start", type=int, required=True, help="First audio number (e.g., 0 for audio000.wav).")
    parser.add_argument("-e", "--end", type=int, required=True, help="Last audio number (e.g., 19 for audio019.wav).")
    parser.add_argument("-s", "--source_path", type=str, required=True, help="Path where WAV files are stored.")
    parser.add_argument("-d", "--destination_path", type=str, required=True, help="Path to save the converted RAW files.")

    args = parser.parse_args()
    
    batch_convert(args.start, args.end, args.source_path, args.destination_path)
