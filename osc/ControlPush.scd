
p = thisProcess.nowExecutingPath.dirname +/+ "template.js";
c = File.new(p, "r").readAllString
c

n = NetAddr("serge-ipad", 8080);
n.sendMsg("/pushInterface", c);
n.sendMsg("/control/setColors", "test1", "#333", "#0F0", "#FFF")

n.sendMsg("/control/createBlankInterface", "my1Interface" "portrait");
n.sendMsg("/control/addWidget", "{'name':'test1', 'type':'Slider', 'bounds':[.0,.5,.5,.5], }");
n.sendMsg("/control/addWidget", "{'name':'test1','bounds':[0, 0,0.75,0.33],'type': 'Slider'}");
n.sendMsg("/control/addWidget", "{'name': 'test1', 'bounds': [ 0, 0, 0.75, 0.33 ], 'type': 'Slider'}");
n.sendMsg("/control/addWidget", ~w1.json);
n.sendMsg("/control/removeWidget", 'test1')

OSCFunc.trace(false);

(
var menu = "{
'name': 'tabButton',
    'type': 'Button',
    'bounds': [.0,.0,0.7,.1],
    'mode': 'toggle',
    'label': 'menu',
},";
n.sendMsg("/control/addWidget", "menu", menu);
)


OSCFunc.trace(true)
OSCFunc.trace(false)

(
~c = ControlApp.new("serge-android", portrait: false);
o = OSCFunc({|m| m.postln;}, "/test1", nil, 5001);

~w1 = ControlHSlider.new(\test1, value: 0.4);
~w1.address;
~w1.json;
~w1.addIndicator;
~c.add(~w1);
{~c.send;~c.sendUpdate}.defer(1);
)

// , \onvaluechange, "label1.changeValue(this.value.toFixed(2));


~w1.value
~w1.width_(0.1)
~w1.height_(0.4)
~w1.sendColors
~w1.fillColor = "#D0D"
~w1.children[0].json
~c.widgets
~c.sendUpdate
~c.clearAll


~w1.dict[\isVertical] = false
~c.addWidget(~w1);
~c.value(~w1, 0.5);
