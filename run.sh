#!/bin/bash

# Build
rm KE1_Bruns_Marvin.zip

mkdir tmp
mkdir tmp/src

cp -r src/main/java/* tmp/src
cd tmp
zip -r KE1_Bruns_Marvin.zip src
mv KE1_Bruns_Marvin.zip ../
cd ..

rm -R tmp


# Run
rm -R KE1_Bruns_Marvin
mkdir KE1_Bruns_Marvin
cd KE1_Bruns_Marvin

mkdir KE1_Konvertiert
unzip ../KE1_Bruns_Marvin.zip
unzip ../KE1_TestBilder.zip

javac --source-path src src/propra/imageconverter/ImageConverter.java -d bin
cd bin

java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_01_uncompressed.tga --output=../KE1_Konvertiert/test_01.propra
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_02_uncompressed.tga --output=../KE1_Konvertiert/test_02.propra
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_03_uncompressed.propra --output=../KE1_Konvertiert/test_03.tga
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_04_uncompressed.propra --output=../KE1_Konvertiert/test_04.tga

java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_01_uncompressed.tga --output=../KE1_Konvertiert/test_01.png
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_02_uncompressed.tga --output=../KE1_Konvertiert/test_02.png
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_03_uncompressed.propra --output=../KE1_Konvertiert/test_03.png
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_04_uncompressed.propra --output=../KE1_Konvertiert/test_04.png

