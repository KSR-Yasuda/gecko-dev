"use strict";

var EXPORTED_SYMBOLS = ["webrequest_test"];

Components.utils.importGlobalProperties(["fetch", "XMLHttpRequest"]);

var webrequest_test = {
  testFetch(url) {
    return fetch(url);
  },

  testXHR(url) {
    return new Promise(resolve => {
      let xhr = new XMLHttpRequest();
      xhr.open("HEAD", url);
      xhr.onload = () => {
        resolve();
      };
      xhr.send();
    });
  },
};
