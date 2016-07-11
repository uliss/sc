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
        params = Dictionary.newFrom(p);
        params[\idx] = type ++ idx;
        params[\type] = type;
        params[\oscPath] = "/nodejs/ui";

        idx_count = idx_count + 1;
        osc = OSCFunc({|m|
            params[\value] = m[1];
            if(action.notNil) {
                action.value(m);
            }
        }, "/sc/ui/" ++ params[\idx], nil, NodeJS.outOscPort);
    }

    add {
        added = true;
        NodeJS.sendMsg("/sc/widget/add", this.asJSON);
        ^this;
    }

    remove {
        added = false;
        NodeJS.sendMsg("/sc/widget/remove", params[\idx]);
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

