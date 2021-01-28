import 'package:flutter/services.dart';
import 'package:flutter/material.dart';

class Bert4Mobile {
  static const platform = const MethodChannel('pytorch.example');
  GlobalKey<ScaffoldState> k;

  Bert4Mobile(GlobalKey<ScaffoldState> k) {
    this.k = k;
  }

  Future<void> loadModel(String text) async {
    k.currentState.showSnackBar(SnackBar(content: Text("Loading model...")));

    try {
      String ret = await platform.invokeMethod('load', text);

      k.currentState.showSnackBar(SnackBar(content: Text("Model loaded")));
    } on PlatformException catch (e) {
      k.currentState.showSnackBar(
          SnackBar(content: Text("A platform error occured: ${e.toString()}")));
    }
  }

  Future<void> runModel(String text) async {
    k.currentState.showSnackBar(SnackBar(content: Text("Running model...")));

    try {
      String ret = await platform.invokeMethod('runModel', text);

      k.currentState.showSnackBar(SnackBar(content: Text("$ret")));
    } on PlatformException catch (e) {
      k.currentState.showSnackBar(
          SnackBar(content: Text("A platform error occured: ${e.toString()}")));
    }
  }
}
