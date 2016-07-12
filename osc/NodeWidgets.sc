NodeJS_Widget {
    classvar idx_count = 1;
    var <idx;
    var <>type;
    var <>params;
    var <>action;
    var added;
    var osc;

    *toUI {
        NodeJS.sendMsg("/sc/redirect", "/ui");
    }

    *new {
        arg type, params = [];
        ^super.new.init(type, params);
    }

    init {
        arg t, p = [];
        type = t;
        idx = idx_count;
        added = false;
        params = Dictionary.new;
        params[\idx] = type ++ idx;
        params = params ++ Dictionary.newFrom(p);
        params[\type] = type;
        params[\oscPath] = "/nodejs/ui";

        idx_count = idx_count + 1;
        osc = OSCFunc({|m|
            params[\value] = m[1];
            if(action.notNil) {
                action.value(m);
            }
        }, "/sc/ui/" ++ this.id, nil, NodeJS.outOscPort);
    }

    colors_ {
        arg border = "#FAA", fill = "#0F0", accent = "#F00";
        params[\colors] = [border, fill, accent];
    }

    add {
        added = true;
        NodeJS.sendMsg("/sc/widget/add", this.asJSON);
        ^this;
    }

    remove {
        added = false;
        NodeJS.sendMsg("/sc/widget/remove", this.id);
        ^this;
    }

    update {
        NodeJS.sendMsg("/sc/widget/update", this.asJSON);
        ^this;
    }

    id {
        ^params[\idx];
    }

    value {
        ^params[\value];
    }

    value_ { |v|
        params[\value] = v;
        if(added) { this.update }
    }

    asJSON {
        ^JSON.toJSON(params);
    }

    label {
        ^params[\label];
    }

    label_ { |txt|
        params[\label] = txt;
    }
}

NodeJS_Knob : NodeJS_Widget {
    *new {
        arg params = [];
        ^super.new("knob", params);
    }
}

NodeJS_Pan : NodeJS_Widget {
    *new {
        arg params = [];
        var p = super.new("pan", params);
        p.label = p.id;
        ^p;
    }
}

NodeJS_Slider : NodeJS_Widget {
    *new {
        arg horizontal = 0, relative = 0, min = 0.0, max = 1.0, size = 180, params = [];
        var p = super.new("slider", [
            \horizontal, horizontal,
            \min, min,
            \max, max,
            \size, size,
            \relative, relative] ++ params);
        ^p;
    }
}

NodeJS_Toggle : NodeJS_Widget {
    *new {
        arg value = 0, size = 60, params = [];
        var p = super.new("toggle", [
            \value, value,
            \size, size] ++ params);
        ^p;
    }
}

NodeJS_Button : NodeJS_Widget {
    *new {
        arg size = 60, params = [];
        var p = super.new("button", [\size, size] ++ params);
        ^p;
    }
}

NodeJS_UI1 {
    var <knob;
    var <toggle;
    var <button;
    var <slider;
    var lines;

    *new {
        arg num = 6;
        ^super.new.init(num);
    }

    init { |n|
        knob = Array.new(n);
        toggle = Array.new(n);
        button = Array.new(n);
        slider = Array.new(n);
        lines = List.new;

        n.do { |i|
            var k, t, b, s;
            var l;

            l = "knob" ++ i;
            k = NodeJS_Knob.new([\idx, l]);
            knob.add(k);
            k.label = l;

            l = "toggle" ++ i;
            t = NodeJS_Toggle.new(params: [\idx, l, \size, 100]);
            toggle.add(t);
            t.label = l;

            l = "button" ++ i;
            b = NodeJS_Button.new(100, [\idx, l]);
            button.add(b);
            b.label = l;

            l = "slider" ++ i;
            s = NodeJS_Slider.new(params:[\idx, l, \width, 100, \height, 150]);
            slider.add(s);
            s.label = l;
        }
    }

    add {
        knob.do { |k| k.add };
        this.addNewline;
        toggle.do { |t| t.add };
        this.addNewline;
        button.do { |b| b.add };
        this.addNewline;
        slider.do { |s| s.add };
    }

    addNewline {
        var nl = NodeJS_Widget.new("newline");
        nl.add;
    }

    remove {
        knob.do { |k| k.remove };
        toggle.do { |t| t.remove };
        button.do { |b| b.remove };
        slider.do { |s| s.remove };
    }
}

