This repository houses a set of tools for re-encoding text files in a 
new character encoding. The tools include a Java API and an Apache Ant 
task. The project exists to fill in a gap in tools for converting the 
encoding of multiple files in a batch format.

## The Java API
The Java API is simple, it is a single method in a single class. If 
you wish to use the Java API directly, simply call the `reencodeFile` 
method of the class `org.charencodingconv.CharacterEncodingConversion`. 
This method re-encodes a single file at a time. See the Java docs for 
this class for further details on usage.

## The Ant Task
Re-encoding of files can be performed with an Ant task, which is included 
in the source code. The Ant task allows you to convert one or more files 
using a fileset. You simply specify the original encoding of the files, 
the encoding you want to convert to and the output directory. The encoding 
of the original files will be converted and then placed in the output 
directory, using the same file names as the originals.

### Usage
First you need to obtain the Jar file with the Ant task from ???. Then 
install the Jar in a place where Ant will be able to find it. See the 
[Ant docs on optional tasks](http://ant.apache.org/manual/install.html#optionalTasks) 
for details.

In your Ant script you will have to define a task using a `taskdef` 
tag. After defining this task, which you can name whatever you wish, 
you can use it in your Ant script just like any other task. See the 
sample script below for an example.

    <?xml version="1.0" encoding="UTF-8"?>
    <project name="sample" default="default">
        <taskdef name="ReEncode" 
                 classname="org.charencodingconv.ant.CharEncodingConverter" 
                 classpath="charencodingconv-0.1.jar"
        />
        <target name="default">
            <ReEncode inputEncoding="US-ASCII" outputEncoding="ISO-8859-1" todir="out">
              <fileset dir="samples" includes=".txt"/>
            </ReEncode>
        </target>
    </project>
In the Ant script above, the task was defined and given the name `ReEncode`. 
The `ReEncode` task was then used to re-encode all of the `*.txt` files 
in the `samples` directory. After running this script the re-encoded files 
will reside in the `out` directory.

## The Gradle Task
The Gradle task is not available in version 0.1, but the plan is to create 
one for version 0.2.

## What About Automatic Detection of the Input Encoding?
The tools that this project provides require that you know the encoding of 
the original files up front. So, the short answer to why is that automatic 
detection is a non-trivial problem. There are many fuzzy situations (i.e. 
a file containing some characters within a single file encoded as UTF-8 and 
others as CP1252) and costs associated with the process (i.e. do you want 
to process the entire file, or just a few bytes to make the decision?). 
There are some tools that can help you do this, like the `file` command 
described below in the section 'Other Tools for Encoding'. It is assumed 
by this project that you know what encoding your input files are in and 
what you want to convert to.

## Other Tools for Encoding
There are solid tools already in existence for doing this sort of work, 
especially in Unix/Linux. In particular, the 
[`file`](http://linux.about.com/library/cmd/blcmdl1_file.htm) command supplies 
the character encoding for a file in the following way:

    $ file -i sample.txt
    sample.txt: text/plain; charset=us-ascii

For conversion, the [`iconv`](http://linux.about.com/library/cmd/blcmdl1_iconv.htm), 
is the tool of choice. It can be used like so to convert a file from CP1252 
encoding to UTF-8:

    $ iconv -f CP1252 -t UTF-8 < sample.txt > sample.txt.out

For a discussion on character encoding in general and a more complete 
list of tools, see the Wikipedia page on 
[Character Encoding](http://en.wikipedia.org/wiki/Character_encoding#cite_note-13). 
For an awesome discussion on how to do character encoding in Ruby and 
how to use `iconv`, see the fantastic 
[blog post](http://blog.grayproductions.net/articles/encoding_conversion_with_iconv) 
by James Gray on the topic.

There is also an auto-detection library called 
["Guess Encoding"](http://guessencoding.codehaus.org/), written by 
Guillame LaForge that is now incorporated into Intellij IDEA. The 
downside of Guess Encoding is that it only looks at a user-defined 
chunk of the file rather than a full analysis of the file. 