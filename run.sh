#!/bin/bash

# Build
rm KE1_Bruns_Marvin.zip

mkdir tmp
mkdir tmp/src

cp -r src/main/java/* tmp/src
cp -r src/test/java/* tmp/test
cd tmp
zip -r KE1_Bruns_Marvin.zip src test
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

java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_01_uncompressed.tga --output=../KE1_Konvertiert/test_01.propra &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_02_uncompressed.tga --output=../KE1_Konvertiert/test_02.propra &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_03_uncompressed.propra --output=../KE1_Konvertiert/test_03.tga &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_04_uncompressed.propra --output=../KE1_Konvertiert/test_04.tga &

java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_01_uncompressed.tga --output=../KE1_Konvertiert/test_01.png &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_02_uncompressed.tga --output=../KE1_Konvertiert/test_02.png &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_03_uncompressed.propra --output=../KE1_Konvertiert/test_03.png &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_04_uncompressed.propra --output=../KE1_Konvertiert/test_04.png &

wait

java -Xmx256m propra.imageconverter.ImageConverter --output=../KE1_TestBilder/test_03_uncompressed2.propra --input=../KE1_Konvertiert/test_03.png
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_03_uncompressed2.propra --output=../KE1_Konvertiert/test_03_2.png
java -Xmx256m propra.imageconverter.ImageConverter --output=../KE1_TestBilder/test_03_uncompressed3.propra --input=../KE1_Konvertiert/test_03.tga

java -Xmx256m propra.imageconverter.ImageConverter --output=../KE1_TestBilder/test_04_uncompressed2.propra --input=../KE1_Konvertiert/test_04.png
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_04_uncompressed2.propra --output=../KE1_Konvertiert/test_04_2.png

java -Xmx256m propra.imageconverter.ImageConverter --output=../KE1_TestBilder/test_01_uncompressed2.tga --input=../KE1_Konvertiert/test_01.png
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_01_uncompressed2.tga --output=../KE1_Konvertiert/test_01_02.png
java -Xmx256m propra.imageconverter.ImageConverter --output=../KE1_TestBilder/test_01_uncompressed3.tga --input=../KE1_Konvertiert/test_01.propra

java -Xmx256m propra.imageconverter.ImageConverter --output=../KE1_TestBilder/test_02_uncompressed2.tga --input=../KE1_Konvertiert/test_02.png
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_02_uncompressed2.tga --output=../KE1_Konvertiert/test_02_02.png

wait
cd ../..
ls -l

# Build Teil 2
rm KE2_Bruns_Marvin.zip

mkdir tmp
mkdir tmp/src

cp -r src/main/java/* tmp/src
cp -r src/test/java/* tmp/test
cd tmp
zip -r KE2_Bruns_Marvin.zip src test
mv KE2_Bruns_Marvin.zip ../
cd ..

rm -R tmp


# Run
rm -R KE2_Bruns_Marvin
mkdir KE2_Bruns_Marvin
cd KE2_Bruns_Marvin

mkdir KE2_Konvertiert
unzip ../KE2_Bruns_Marvin.zip
unzip ../KE2_TestBilder.zip
unzip ../KE2_TestBilder_optional.zip

javac --source-path src src/propra/imageconverter/ImageConverter.java -d bin
cd bin

java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_01_uncompressed.tga --output=../KE2_Konvertiert/test_01.propra --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --output=../KE2_Konvertiert/test_01_uncompressed.png --input=../KE2_Konvertiert/test_01.propra
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_01_uncompressed.tga --output=../KE2_Konvertiert/test_01.tga --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --output=../KE2_Konvertiert/test_01_uncompressed_2.tga --input=../KE2_Konvertiert/test_01.propra --compression=uncompressed

java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_02_rle.tga          --output=../KE2_Konvertiert/test_02.propra --compression=uncompressed
java -Xmx256m propra.imageconverter.ImageConverter --output=../KE2_Konvertiert/test_02.propra          --input=../KE2_Konvertiert/test_02.propra --compression=uncompressed
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_02_rle.tga          --output=../KE2_Konvertiert/test_02.png

java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_03_uncompressed.propra --output=../KE2_Konvertiert/test_03.tga --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --output=../KE2_Konvertiert/test_03.png --input=../KE2_Konvertiert/test_03.tga
java -Xmx256m propra.imageconverter.ImageConverter --output=../KE2_Konvertiert/test_03_rle.propra --input=../KE2_Konvertiert/test_03.tga --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_Konvertiert/test_03_rle.propra --output=../KE2_Konvertiert/test_03_2.tga --compression=rle

java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_04_rle.propra          --output=../KE2_Konvertiert/test_04.tga --compression=uncompressed
java -Xmx256m propra.imageconverter.ImageConverter --output=../KE2_Konvertiert/test_04.png          --input=../KE2_Konvertiert/test_04.tga
java -Xmx256m propra.imageconverter.ImageConverter --output=../KE2_Konvertiert/test_04_2.tga          --input=../KE2_Konvertiert/test_04.tga --compression=uncompressed


java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_05_base32.tga.base-32    --decode-base-32 &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_06_base32.propra.base-32 --decode-base-32 &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_02_rle.tga               --encode-base-32 &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_04_rle.propra            --encode-base-32 &

wait

java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-2_a.propra.base-n --decode-base-n &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-2_b.propra.base-n --decode-base-n &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-4.propra.base-n --decode-base-n &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-8.propra.base-n --decode-base-n &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-64.propra.base-n --decode-base-n &

wait

java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-2_a.propra --output=../KE2_Konvertiert/test_base-2_a.tga &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-2_b.propra --output=../KE2_Konvertiert/test_base-2_b.tga &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-4.propra --output=../KE2_Konvertiert/test_base-4.tga &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-8.propra --output=../KE2_Konvertiert/test_base-8.tga &
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-64.propra --output=../KE2_Konvertiert/test_base-64.tga &

wait

java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_grosses_bild.propra --output=../KE2_Konvertiert/test_grosses_bild.tga --compression=rle &
# java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_grosses_bild.propra --output=../KE2_Konvertiert/test_grosses_bild2.tga --compression=uncompressed &



wait
cd ../..
ls -l



# Build Teil 3
rm KE3_Bruns_Marvin.zip

mkdir tmp
mkdir tmp/src

cp -r src/main/java/* tmp/src
cp -r src/test/java/* tmp/test
cd tmp
zip -r KE3_Bruns_Marvin.zip src test
mv KE3_Bruns_Marvin.zip ../
cd ..

rm -R tmp


# Run
rm -R KE3_Bruns_Marvin
mkdir KE3_Bruns_Marvin
cd KE3_Bruns_Marvin

mkdir KE3_Konvertiert
unzip ../KE3_Bruns_Marvin.zip
unzip ../KE3_TestBilder.zip

javac --source-path src src/propra/imageconverter/ImageConverter.java -d bin
cd bin


java -Xmx256m propra.imageconverter.ImageConverter --input=../KE3_TestBilder/test_01_uncompressed.tga  --output=../KE3_Konvertiert/test_01.propra --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE3_TestBilder/test_02_rle.tga           --output=../KE3_Konvertiert/test_02.propra --compression=uncompressed
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE3_TestBilder/test_03_uncompressed.propra --output=../KE3_Konvertiert/test_03.tga  --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE3_TestBilder/test_04_rle.propra          --output=../KE3_Konvertiert/test_04.tga  --compression=uncompressed
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE3_TestBilder/test_05_huffman.propra      --output=../KE3_Konvertiert/test_05.tga  --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE3_Konvertiert/test_05.tga      --output=../KE3_Konvertiert/test_05_rehuf.propra  --compression=huffman
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE3_Konvertiert/test_05_rehuf.propra      --output=../KE3_Konvertiert/test_05_rehuf.tga  --compression=uncompressed

wait
cd ../..
ls -l
