import cv2
import numpy
import os
from argparse import ArgumentParser

#from tensorflow.keras.preprocessing.image import ImageDataGenerator
#from tensorflow.keras.optimizers import RMSprop
#from tensorflow.keras.utils import to_categorical
#from tensorflow.keras.utils import plot_model
#from tensorflow.keras.models import model_from_json
#cd D:\GP\Classification
#python cnn.py -a train -n hosam
# Parse
from PIL import Image
from keras.engine.saving import model_from_json
from keras.optimizers import RMSprop
from keras.preprocessing import image

from keras.utils import to_categorical, plot_model
from keras_preprocessing.image import ImageDataGenerator
import myface
from myface.models.keras import RandomNeuralClassifier
from myface.utils import load_labeled_images, resize_image, train_test_split_labled_images, normalize

argument_parser = ArgumentParser()

argument_parser.add_argument('-a', '--action', required = True, help = 'action to perform, either train, test, or show')
argument_parser.add_argument('-n', '--name', required = True, help = 'name of the model on which actions will be performed')
argument_parser.add_argument('-d', '--detection', type = str, default="cnn", help='detection method to be performed')

args = vars(argument_parser.parse_args())
resize_width, resize_height = 48, 48
#names=['cat','dog']
#names=['aleff','zay','seen','sheen','saad','dhad','ta','dha','ain','ghain','fa','bb','gaaf','kaaf','laam','meem','nun','ha','waw','ya','taa','thaa','jeem','haa','khaa','dal','thal','ra']
#print(names[0],"  jjjjjjjjjjj  ",names[1])
if args['action'] == 'train':
	# Load
	labled_images, names = load_labeled_images('D:\\CNN_Data-20181006T133304Z-001\\CNN_Data\\training_set', sep='_')
	# Resize
	for label in labled_images:
		for i in range(0, len(labled_images[label])):
			labled_images[label][i] = resize_image(labled_images[label][i], resize_width, resize_height)
	# Split 80% for training, 20% for testing
	x_train, y_train, x_test, y_test = train_test_split_labled_images(labled_images, test_size = 0.2)

	# Preprocess for keras models
	x_train = numpy.asarray(x_train)
	x_test = numpy.asarray(x_test)

	y_train = to_categorical(y_train, len(names))
	y_test = to_categorical(y_test, len(names))

	# Normalization
	x_train = normalize(x_train)
	x_test = normalize(x_test)

	# Training / Testing parameters
	batch_size, epochs, verbose = 32, 32, 1
	steps_per_epoch = int((x_train.shape[0] / batch_size))
	print(x_train.shape[0], "uuuuuuuu", steps_per_epoch)

	print("khlas")

# Fake Data Generation
	FakeDataGenerator = ImageDataGenerator(rotation_range = 15, zoom_range = 0.1, width_shift_range = 0.1, height_shift_range = 0.1, horizontal_flip = True)
	FakeDataGenerator.fit(x_train)

if args['action'] == 'test' or args['action'] == 'show' or args['action'] == 'yalla':

	# Load Model
	with open(args['name'] + '.json', 'r') as json_file:
		model = model_from_json(json_file.read())

	model.load_weights(args['name'] + '.h5')

if args['action'] == 'train':

	# Model
	if args['detection'] == 'nn':
		model = RandomNeuralClassifier.build(input_shape = (resize_width, resize_height, 3), classes = len(names))

	elif args['detection'] == 'cnn':
		model = myface.models.keras.SmallVGGNet.build(input_shape = (resize_width, resize_height, 3), classes=len(names))

model.compile(loss="categorical_crossentropy", optimizer = RMSprop(), metrics=["accuracy"])

if args['action'] == 'train':

	#model.fit(x_train, y_train, batch_size = batch_size, epochs = epochs, verbose = verbose)
	model.fit_generator(FakeDataGenerator.flow(x_train, y_train, batch_size = batch_size), epochs=epochs,verbose=verbose,steps_per_epoch=8000)

	# Save Model
	model_json = model.to_json()
	with open(args['name'] + '.json', 'w') as json_file:
		json_file.write(model_json)

	model.save_weights(args['name'] + '.h5')

	#plot_model(model, show_shapes = True, to_file=args['name'] + '.png')

	# Test
	(loss, accuracy) = model.evaluate(x_test, y_test, batch_size = batch_size, verbose = verbose)
	print("Accuracy: ", accuracy * 100)

# Plot
if args['action'] == 'show':
	i=0
	for image in x_test:
		pred_probs = model.predict(image[numpy.newaxis, ...])
		pred = pred_probs.argmax()
		g = model.predict_proba(image[numpy.newaxis, ...])
		image = resize_image(image, 244, 244)
		print(image)
		#print(str(names[pred])," ",nameeee[i]," ",g)
		#cv2.putText(image, str(names[pred]), (5, 35), cv2.FONT_HERSHEY_SIMPLEX, 1.5, (255, 255, 255), 2)
		#cv2.imshow('Testing', image)
		cv2.waitKey()
		if cv2.waitKey(1) & 0xFF == ord('q'):
			break
		i=i+1

if args['action'] == 'yalla':
	tt=[]
	for filename in os.listdir('D:\\cats'):
		im =cv2.imread('D:\\cats\\'+filename)
		#cv2.imwrite('D:\\GP\\sign2text-master\\sign2text-master\\cat_or_dogss.jpg',im)
		im=resize_image(im, resize_width, resize_height)
		tt.append(im)
	tt = numpy.asarray(tt)
	tt = normalize(tt)
	for image1 in tt:
		pred_probs = model.predict(image1[numpy.newaxis, ...])
		g=model.predict_proba(image1[numpy.newaxis, ...])
		pred = pred_probs.argmax()
		print(str(names[pred]))
