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
		img = cv2.imread(file_path)
		dets = cv2.CascadeClassifier("haarcascade_frontalface_default.xml").detectMultiScale(
  			cv2.cvtColor(img, cv2.COLOR_BGR2GRAY),
    		scaleFactor = 1.1,
		    minNeighbors = 5,
		    minSize = (32, 32),
			flags = cv2.CASCADE_SCALE_IMAGE
		)
		for (x, y, w, h) in dets:
			try:
				cropped_img = img[y:(y + h), x:(x + w)]
				cv2.imwrite(dest_dir_path + str(n_faces) + '.jpg', cropped_img)
				n_faces += 1
			except:
				print('Invalid face!')
	except:
		print('Invalid img!')
		continue;

print('Number of faces extracted: ' + str(n_faces) + ' face!')

