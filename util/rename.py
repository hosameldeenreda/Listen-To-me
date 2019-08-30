import sys
import os

src_dir_path = sys.argv[1]

file_exts = ('.jpg', '.JPG', '.jpeg', '.JPEG')
file_names = [file_name for file_name in os.listdir(src_dir_path) if file_name.endswith(file_exts)]
file_paths = [os.path.join(src_dir_path, file_name) for file_name in file_names]

n = int(sys.argv[2])
label = sys.argv[3]
name = sys.argv[4]
for file_path in file_paths:
	new_file_name = src_dir_path + "\\" +  str(n) + "_" + str(label) + "_" + name + ".jpg"
	print(new_file_name)
	os.rename(file_path, new_file_name)
	n += 1