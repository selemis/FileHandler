# About the project 

It is a graphical utility for performing operations on files under a folder.

## Application description

The utility has a graphical user interface that loads the files under a folder and displays them in a grid. There is a text area where you can write code that performs the operations on the files.

The code can be written in groovy of java. There are two special variables

* **f** which stands for the File object of each row of the grid.
* **r** for the row of the grid, starting counting from 0

In addition the last expression of the code is returned in the column named result, for each file. Everytime the **"Perform"** button is pressed the code is executed for every row in the grid and the result column is updated.

## Examples ##

### Getting the file name

```groovy
f.name
```

It returns the filename only for the file. This works because that is groovy code which actually calls the getName() method on the file object. The above code does not do anything to file. It is just displaying the result in the Result column.

### Using the row number to sort the files

```groovy
"${r+1}_${f.name}"
```

This will add the number of row, followed by and underscore, in front of the file name.

### Copying files to another folder.
	
```groovy
dest = "/home/user/temp/${f.name}"
f.copy(dest)
dest
```

We define a destination variable named dest. Then we use the File object method copy, which will copy every file to the temp folder with the same name. Finally we return the dest variable so that it can be displayed in the **Result** column.

row = "${r+1}".padLeft(3,'0')
dest = "/home/user/temp/${row}_${f.name}"
dest

## Build in functions#

At the moment there are a couple of build-in functions:

* ext(File)
    * It returns the extension of the file  
* withoutExt(File)
    * It returns the filename without the extension
    
File class has also been enriched with method copy(String). The argument is the file destination.    

## Building the project

The project uses gradle for its build process. In order to run the application you need to have gradle installed and run

 ```bash
 #To run the application
 gradle run
 
 #To build a distribution
 gradle build
 ```