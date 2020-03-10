# boxFileExportPublic
Box File Export Public

Project I used to export files from Box to my network. It wil loop through the source folder specified in the application.properties It creates one thread per root folder up to the max number of threads specified in the same config file. Note: Box throttled me between 5 and 6 threads.

I removed all the code and configuration that were for my company's purpose including box config from the dev page ( private keys, public key, etc. ) Thus I have not tested the application since removing all my personal stuff.

The code should work without issues though once you plug in your box config data.

I made the appliation multi-threaded as one thread was going to slow for me. I've never done this before so I have no idea if it's thread safe so to speak so be careful with that. Also, I believe it could be made faster if you created a thread every time you recursed throug a sub folder instead of creating one thread per root folder.
I didn't have time to mess with that.

Questions feel free to email me at srieger@riegergroup.com
