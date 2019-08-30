import sys
import os
import dlib
import cv2

src_dir_path = sys.argv[1]

file_exts = ('.jpg', '.JPG', '.jpeg', '.JPEG')
file_names = [file_name for file_name in os.listdir(src_dir_path) if file_name.endswith(file_exts)]
file_paths = [os.path.join(src_dir_path, file_name) for file_name in file_names]

n_files = 0
for file_path in file_paths:
	n_files += 1
	print(file_path + '\t' + str(n_files) + ' / ' + str(len(file_paths)))
	cv2.imwrite(file_path, cv2.resize(cv2.imread(file_path), (48, 48)) )

print("All imgs resized successfully!")