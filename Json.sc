JSON {
    *convert {|obj| ^JSON.toJSON(obj); }

    *toJSON { |obj|
        switch(obj.class.name,
            \Dictionary, {^JSON.dictToJSON(obj)},
            \List, { ^JSON.listToJSON(obj) },
            \Array, { ^JSON.listToJSON(obj) },
            \Association, { ^JSON.pairToJSON(obj)},
            \Set, {^JSON.listToJSON(obj)},
            {
                if(obj.isKindOf(Number)) {
                    ^obj.asString;
                }

                ^obj.asString.quote;
            }
        );
    }

    *pairToJSON { |pair|
        pair.postln;

    }

    *dictToJSON { |dict|
        var str = "{";
        var new_lst = List.new;
        dict.keysValuesDo { |k, v|
            new_lst.add(JSON.toJSON(k) ++ ":" + JSON.toJSON(v));
        };

        str = str ++ new_lst.join(", ");

        str = str ++ "}";
        ^str;
    }

    *listToJSON { |lst|
        var str = "[";
        var new_lst = List.new;
        lst.do { |v|
            new_lst.add(JSON.toJSON(v));
        };
        str = str + new_lst.join(", ");
        str = str + "]";
        ^str;
    }
}