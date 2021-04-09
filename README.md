# Encoder
![Image text](doc/html/icon/unictLogoExt.svg) 
# User's Guide
## Encoder v1.0
### Introduction
Encoder is a Java program for data encoding and decoding without loss of information. 

In particular, it is possible:

- Encode a file using the Run Length encoding algorithm (optimized with flag);
- Encode a file using the LZW (Lempel-Ziv-Welch) encoding algorithm;
- Unzip a previously compressed file using the decompression function of one of the two algorithms (it will be chosen automatically).

For more information, see [Release Notes](#release-notes).

### Usage
You can run the program:

- Starting the executable JAR file;
- Or by compiling and running the code from a terminal or any Java compatible IDE.

For a more detailed guide, see [Installation Notes](#installation-notes).

### Operation of the program
Its operation is divided into 3 steps:

- First of all, press the "Open" button (on the right), in order to start the file manager, through which you can choose the file that most interests you to compress or decompress;
- Then select between three possible modes: RunLength encoding, LZW encoding, decoding;
- Finally press the start button (called "coding" or "decoding") and wait for the upload to finish. When finished, the destination directory will appear on the screen.

# Release Notes
## Encoder v1.0
### Programming techniques
Encoder is entirely written in Java language. For the graphics part, take advantage of the Java Swing graphics library. The code implements the encoding and decoding functions of the lossless compression algorithms RunLength (with flag) and LZW (from the creators Lempel-Ziv-Welch).

#### Run-Length encoding
The RLE encoding (Run-Length encoding) algorithm looks for a series of at least 4 equal elements in the file to be compressed (in a bitmap image, for example, these conditions are obtained in uniformly colored sections of the image), and replaces it with three elements: a special character (called flag), which indicates the beginning of a coded series, the element of which the series is composed and finally the number of times it repeats.

This algorithm works well for data that has a lot of repetitions in it. The problem is that the flag can also be contained in the file and must therefore be encoded even if in a series of an element. This can lead to encoded files that take up more memory than the original file. To limit this occurrence, before starting the encoding, the "flagConveniente" function assigns the value of the element with fewer appearances to the flag variable. If the file size is greater than or equal to 10000 bytes, to avoid slowdowns, the function will work on 10000 elements taken as a sample.

The RLE decoding algorithm, every time it finds the triad (flag, a, n), replaces it with a series of n elements a.

#### LZW coding
The LZW coding algorithm is based on the fact that a sequence of elements contains within it repeated subsequences. To exploit this fact, a dictionary is created, which initially presents all the single possible elements that may appear in the sequence (alphabet). Whenever the algorithm finds any sequences that have never appeared before, it adds them to the dictionary and assigns them an index. Finding a subsequence that has never appeared before indicates that the same subsequence, after removing the last element, is found in the dictionary and is therefore replaced by its identifying index. To speed up the numerous searches, in the coding phase, the dictionary is built through a modified binary search tree, so as to be, from the beginning, composed of the elements of the alphabet and in order to facilitate the extraction of the indexes. For decompression, the dictionary is initially composed only of the elements of the alphabet. At each iteration, the algorithm reads an index and replaces it with the equivalent sequence. It also adds to the dictionary this latter sequence linked to the one corresponding to the next index. There is a case, however, where the next symbol is not present in the dictionary. Usually this happens in input from the form abababab and, in particular, when the subsequence begins and ends with the same character. So, to deal with this exception, you simply need to take the last subsequence obtained and chain it with its first element, instead of following the normal procedure.

In the decoding function, not having to search anymore, the dictionary has been implemented through a vector, which has a more efficient insertion function and variable capacity.

Since the int type has a limited range, in order to process large files, it was necessary to set a maximum capacity of the dictionary. When this limit is reached, the dictionary is emptied and the LZW algorithm restarts from its initial state, continuing from the point in the file where it left off.

### Testing
Various tests have been carried out on Linux (in particular Ubuntu 18.10) and Windows and in both cases the program does not seem to present problems.

### Ideas for future developments
- Introduce additional compression algorithms.
- Add the possibility to automatically choose the encoding algorithm based on an analysis of the input file.

### Software architecture
The description of the software structure can be found [here](https://www.dmi.unict.it/archelab/projects/encoder/doc/NaturalDocs/ND%20Config/index.html).

# Installation notes
## Encoder v1.0
### System requirements
From the hardware point of view, Encoder does not require any particular specifications. The question changes from a software point of view as Encoder is a program written in Java language. For this reason it needs external tools for both compilation and execution on each of the major operating platforms (Linux, Microsoft Windows and MacOs).

### Compilation and execution
After unpacking the distribution, to compile the sources or run the program, you need to download and install the Java SE Development Kit, regardless of the operating system. Downloads and other documentation are available [here](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html). On Linux (Debian based), alternatively, you can simply run the command "sudo apt-get install openjdk-11-jre" from the terminal. To compile, you need to open the encoder10\src\frame1 folder of the distribution from the terminal and execute the command "javac \*.java". Encoder doesn't need to be installed, it just needs to be run. 

You can run Encoder in 2 ways:

- You can use the Encoder.jar executable file, located in the encoder10\bin folder of the distribution. On Windows just double left click, while on Linux or MacOs from the terminal you need to go to the path where the JAR file is and use the command "java -jar Encoder.jar";
- Or, by opening the terminal in the encoder10\bin folder, the command "java -classpath. Frame1.Interface" can be used.
