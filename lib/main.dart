import 'dart:io';

import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: HomeScreen(),
    );
  }
}

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  MethodChannel _methodChannel = MethodChannel('test.file.path');

  String methodResult = '';
  String filePathResult = '';
  String fileNameResult = '';
  String methodFilePathResult = '';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('File Delete Native'),
      ),
      body: Container(
        child: Center(
          child: Column(
            children: [
              Spacer(),
              //Text('File Path Name \n\n\n $methodResult \n\n\n\nFile Name: $fileNameResult\n\n\n\nFile Method Return Result: $methodFilePathResult'),
              Text('File Path:\n\n\n $methodFilePathResult'),
              SizedBox(
                height: 12,
              ),
              ElevatedButton(
                onPressed: () async {
                  // FilePickerResult result =
                  //     await FilePicker.platform.pickFiles();
                  // if (result != null) {
                  //   File file = File(result.files.single.path);
                  //   setState(() {
                  //     methodResult = file.absolute.path;
                  //
                  //     String filename = methodResult
                  //         .substring(methodResult.lastIndexOf("/") + 1);
                  //
                  //     fileNameResult = filename;
                  //
                  //     print(file.absolute.path);
                  //     print(filename);
                  //   });
                  // }

                  _methodChannel.invokeMethod('getPath', <String, dynamic>{
                    'extraFile': null,
                    'fileType': 'image',
                    // 'fileType': 'audio',
                    // 'fileType': 'video',
                    // 'fileType': 'file',
                    // 'fileType': 'apk',
                  }).then((value) {
                    setState(() {
                      methodFilePathResult = value;
                      print(value);
                    });
                  });
                },
                child: Text('Pick file'),
              ),
              Spacer(),
            ],
          ),
        ),
      ),
    );
  }
}
