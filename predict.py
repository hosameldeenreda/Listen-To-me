import numpy
import os
from argparse import ArgumentParser
import cv2

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

json_file = open('hosam.json', 'r')
loaded_model_json = json_file.read()
json_file.close()
model = model_from_json(loaded_model_json)
test_image = image.load_img('D:\\GP\\sign2text-master\\sign2text-master\\cat_or_dog.jpg')
pred_probs = model.predict(image[numpy.newaxis, ...])
pred = pred_probs.argmax()
image = resize_image(image, 244, 244)
cv2.putText(image, str(names[pred]), (5, 35), cv2.FONT_HERSHEY_SIMPLEX, 1.5, (255, 255, 255), 2)
cv2.imshow('Testing', image)
