import sys
import os
import dlib
import cv2

src_dir_path = sys.argv[1]
dest_dir_path = sys.argv[2]

file_exts = ('.jpg', '.JPG', '.jpeg', '.JPEG')
file_names = [file_name for file_name in os.listdir(src_dir_path) if file_name.endswith(file_exts)]
file_paths = [os.path.join(src_dir_path, file_name) for file_name in file_names]

n_files, n_faces = 0, 0
for file_path in file_paths:
	n_files += 1
	print(file_path + '\t' + str(n_files) + ' / ' + str(len(file_paths)))
	try:
		img = dlib.load_rgb_image(file_path)
		dets = dlib.get_frontal_face_detector()(img, 1)
		for i, det in enumerate(dets):
			try:
				cropped_img = img[det.top():det.bottom(), det.left():det.right()]
				dlib.save_image(cropped_img, dest_dir_path + str(n_faces) + '.jpg')
				n_faces += 1
			except:
				print('Invalid face!')
	except:
		print('Invalid img!')
		continue;

print('Number of faces extracted: ' + str(n_faces) + ' face!')

