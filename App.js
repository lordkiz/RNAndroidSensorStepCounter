/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {useState} from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  NativeEventEmitter,
} from 'react-native';

import RNAndroidSensorStepCounter from './RNAndroidSensorStepCounter';

async function isReady() {
  try {
    const ready = await RNAndroidSensorStepCounter.ready();

    if (!ready) {
      RNAndroidSensorStepCounter.startService();
      return true;
    }
    return ready;
    
  } catch (e) {
    console.log(e);
    return false;
  }
}

const App = () => {
  const [step, setStep] = useState(1);

  const emitter = new NativeEventEmitter(RNAndroidSensorStepCounter);
  emitter.addListener('steps', stepObject => {
    setStep(stepObject.steps);
  });

  const ready = isReady();

  return (
    <TouchableOpacity
      onPress={() => {
        if (!ready) RNAndroidSensorStepCounter.startService();
      }}>
      <View>
        <Text>Steps: {step} steps</Text>
      </View>
    </TouchableOpacity>
  );
};


export default App;
