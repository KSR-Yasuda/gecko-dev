ChromeUtils.import("resource://gre/modules/NetUtil.jsm");

var listener = {
  expect_failure: false,
  QueryInterface: function listener_qi(iid) {
    if (iid.equals(Ci.nsISupports) ||
        iid.equals(Ci.nsIUnicharStreamLoaderObserver)) {
      return this;
    }
    throw Components.results.NS_ERROR_NO_INTERFACE;
  },
  onDetermineCharset : function onDetermineCharset(loader, context, data)
  {
    return "us-ascii";
  },
  onStreamComplete : function onStreamComplete (loader, context, status, data)
  {
    try {
      if (this.expect_failure)
        Assert.ok(!Components.isSuccessCode(status));
      else
        Assert.equal(status, Components.results.NS_OK);
      Assert.equal(data, "");
      Assert.notEqual(loader.channel, null);
      tests[current_test++]();
    } finally {
      do_test_finished();
    }
  }
};

var current_test = 0;
var tests = [test1, test2, done];

function run_test() {
  tests[current_test++]();
}

function test1() {
  var f =
      Cc["@mozilla.org/network/unichar-stream-loader;1"].
      createInstance(Ci.nsIUnicharStreamLoader);
  f.init(listener);

  var chan = NetUtil.newChannel({
    uri: "data:text/plain,",
    loadUsingSystemPrincipal: true
  });
  chan.asyncOpen2(f);
  do_test_pending();
}

function test2() {
  var f =
      Cc["@mozilla.org/network/unichar-stream-loader;1"].
      createInstance(Ci.nsIUnicharStreamLoader);
  f.init(listener);

  var chan = NetUtil.newChannel({
    uri: "http://localhost:0/",
    loadUsingSystemPrincipal: true
  });
  listener.expect_failure = true;
  chan.asyncOpen2(f);
  do_test_pending();
}

function done() {
}
