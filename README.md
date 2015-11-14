# Brainfuck-IDE
The Brainfuck IDE is a nice little working environment to practise and play around with Brainfuck.

Usage
-----
The IDE has 4 compartments.
* Top left = The workspace
* Bottom left = Output
* Top right = Controls
* Bottom right = Memory
 
####The workspace
The workspace is where you enter the Brainfuck code.
Ook! is supported in version 3.0 onwards

####Output
Pressing the Run! button executes the Brainfuck code. Any comments that are not written in Brainfuck will not be parsed. The output will be printed here.

####Controls
Pretty self explanatory, the controls control the ins and outs of the IDE
* Run! - Runs the code
* Stop - Stops any running code (e.g. code awaiting a character input)
* Reset - Clears the memory and output
* Format - Formats the Brainfuck code into a format which is easier to read
* Unformat - Formats the Brainfuck code into a line of condensed characters
* Wrapping mode/Non-wrapping mode - Enables/Disables wrapping. When disabled, cells that go below 0 will stay at 0, likewise, cells that go above the maximum (e.g. 255) will stay at 255
* Set memory (Cells) - Sets the number of cells (Defaults to 384)
* Input: - The input box whenever an input character (,) is reached
* Set cell size (Bits) - Sets the number of bits each cell can take.
  * 8 bits (0 to 255)
  * 16 bits (0 to 65535)
  * 32 bits (0 to 4294967295)
* Character code finder - Finds the decimal ASCII code for that certain character

####Memory
This shows the cells and the data inside each cell. The pointer is highlighted (version 3.0 onwards), or shown in the status bar at the bottom of the application

Downloads
---------
Download the latest release [here](https://github.com/Skepter/Brainfuck-IDE/releases)
