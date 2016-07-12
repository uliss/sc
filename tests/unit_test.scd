(
var tests;
UnitTestScript.clear;
pathMatch(thisProcess.nowExecutingPath.dirname +/+ "tests/*_unittest.scd").do { |path|
    var utest;
    utest = UnitTestScript.new(path.basename.splitext()[0].drop(-9).toUpper.quote, path);
    utest.runScript;
});
)