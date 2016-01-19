ControlApp {
    var host;
    var port;
    var name;
    var portrait;
    var <addr;
    var <dest;
    var <widgets;
    var colors;

    *new {
        arg host, port = 8080, name = "scInterface", portrait = true, dHost = "serge-macbook", dPort = 5001;
        ^super.new.init(host, port, name, portrait, dHost, dPort);
    }

    init {
        arg oscHost, oscPort, nameI, isPort, dHost, dPort;
        host = oscHost;
        port = oscPort;
        name = nameI;
        portrait = isPort;
        widgets = List.new;

        dest = (dHost ++ ":" ++ dPort);
        dest.postln;

        addr = NetAddr(host, port);
        addr.sendMsg("/control/pushDestination", dest);
        addr.sendMsg("/control/createBlankInterface", nameI, "portrait");

        colors = Dictionary.new;
        colors["Slider"] = ["#2c3e50", "#0dc3c7", "#95a5a6"];
        colors["Label"] =  [nil, "#FFF", "#95a5a6"];
    }

    add {
        arg w;
        w.parent = this;
        widgets.add(w);
    }

    send {
        widgets.do {|w|
            w.sendAdd;
        }
    }

    sendUpdate {
        widgets.do {|w|
            w.sendUpdate;
        }
    }

    clearAll {
        widgets.do {|w|
            w.sendRemove;
        };

        widgets = [];
    }

    backgroundColor {
        arg type;
        ^colors[type][0]
    }

    fillColor {
        arg type;
        ^colors[type][1]
    }

    strokeColor {
        arg type;
        ^colors[type][2]
    }
}

ControlWidget {
    var <>parent;
    var type;
    var <bounds;
    var <value;
    var <name;
    var <dict;
    var <min;
    var <max;
    var background_color;
    var stroke_color;
    var fill_color;
    var <children;

    *new {
        arg id, type, bounds, initValue = nil, parent = nil, min = 0, max = 1;
        ^super.new.init(id, type, bounds, initValue, parent, min, max);
    }

    address_ {
        arg path = nil;
        dict[\address] = path ? ("/" ++ name);
    }

    address {
        ^dict[\address] ? ("/" ++ name);
    }

    sendAdd {
        if(parent.notNil) {
            parent.addr.sendMsg("/control/addWidget", this.json);
            children.do {|c|
                c.parent = parent;
                c.sendAdd;
            };
        };
    }

    sendUpdate {
        if(parent.notNil) {
            if(value.notNil) {
                parent.addr.sendMsg(this.address, value);
            }
        }
    }

    sendRemove {
        if(parent.notNil) {
            parent.addr.sendMsg("/control/removeWidget", name);
        }
    }

    sendBounds {
        if(parent.notNil) {
            parent.addr.sendMsg("/control/setBounds", name, bounds.left, bounds.top, bounds.width, bounds.height);
        }
    }

    sendColors {
        if(parent.notNil) {
            "colors: %:%:%\n".postf(this.backgroundColor, this.fillColor, this.strokeColor);
            parent.addr.sendMsg("/control/setColors", name, this.backgroundColor, this.fillColor, this.strokeColor);
        }
    }

    x_ {
        arg x;
        bounds.left = x;
        this.sendBounds;
    }

    y_ {
        arg y;
        bounds.top = y;
        this.sendBounds;
    }

    width_ {
        arg w;
        bounds.width = w;
        this.sendBounds;
    }

    height_ {
        arg h;
        bounds.height = h;
        this.sendBounds;
    }

    pos_ {
        arg x, y;
        this.setX(x);
        this.setY(y);
    }

    setArgs {
        arg d;
        d.postln;
        d.asDict.keysValuesDo {|k,v|
            dict[k] = v;
        }

        ^this;
    }

    backgroundColor {
        ^ background_color ?? {
            if(parent.notNil) { parent.backgroundColor(type) } { "#000" }
        }
    }

    fillColor {
        ^ fill_color ?? {
            if(parent.notNil) { parent.fillColor(type) } { "#555" }
        }
    }

    fillColor_ {
        arg c;
        fill_color = c;
        this.sendColors;
    }

    strokeColor {
        ^ stroke_color ?? {
            if(parent.notNil) { parent.strokeColor(type) } { "#CCC" }
        }
    }

    init {
        arg name_, type_, bounds_, init_value_, parent_, min_, max_;
        name = name_;
        type = type_;
        bounds = bounds_;
        value = init_value_;
        parent = parent_;
        min = min_;
        max = max_;
        children = List.new;

        dict = Dictionary.new;
        dict[\type] = type_;
        ^this;
    }

    json {
        dict[\bounds] = bounds;
        dict[\name] = name;
        dict[\min] = min;
        dict[\max] = max;
        dict[\stroke] = this.strokeColor;
        dict[\backgroundColor] = this.backgroundColor;
        dict[\color] = this.fillColor;
        ^ControlWidget.toJSON(dict);
    }

    *toJSON {
        arg obj;
        switch(obj.class.name,
            \Dictionary, {^ControlWidget.dictToJSON(obj)},
            \Rect, { ^ControlWidget.rectToJSON(obj) },
            \True, {^"true"},
            \False, {^"false"},
            \Nil, {^""},
            {
                if(obj.isKindOf(Number)) {
                    ^obj.asString;
                }

                ^obj.asString.shellQuote;
            }
        );
    }

    *dictToJSON {
        arg dict;
        var str = "{";
        var new_lst = List.new;
        dict.keysValuesDo { |k, v|
            new_lst.add(ControlWidget.toJSON(k) ++ ":" + ControlWidget.toJSON(v));
        };

        str = str ++ new_lst.join(", ");

        str = str ++ "}";
        ^str;
    }

    *rectToJSON {
        arg rect;
        ^[rect.left, rect.top, rect.width, rect.height].asString;
    }
}

ControlSlider : ControlWidget {
    *new {
        arg id, bounds, value, vertical, min, max, args;
        ^super.new(id, "Slider", bounds, value, nil, min, max).setArgs(args ++ [\isVertical, vertical]);
    }

    addIndicator {
        var id = name ++ 'Label';
        children.add(ControlLabel.new(id, bounds, "0"));
        this.setArgs([\onvaluechange, "%.changeValue(this.value.toFixed(2));".format(id)]);
    }
}

ControlVSlider : ControlSlider {
    *new {
        arg id, bounds = Rect(0.05, 0.05, 0.1, 0.4), value = 0, min = 0, max = 1, args = [];
        ^super.new(id, bounds, value, true, min, max, args);
    }
}

ControlHSlider : ControlSlider {
    *new {
        arg id, bounds = Rect(0.05, 0.05, 0.6, 0.05), value = 0, min = 0, max = 1, args = [];
        ^super.new(id, bounds, value, false, min, max, args);
    }
}

ControlLabel : ControlWidget{
    *new {
        arg id, bounds, label, args = [];
        ^super.new(id, "Label", bounds).setArgs(args ++ [\value, label]);
    }



}