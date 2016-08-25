JSON {
    *convert {|obj| ^JSON.toJSON(obj); }

    *toJSON { |obj|
        if(obj.isKindOf(Dictionary)) { ^JSON.dictToJSON(obj) };
        if(obj.isKindOf(Set)) { ^JSON.listToJSON(obj) };
        if(obj.isKindOf(Boolean)) { ^obj.asString };
        if(obj.isKindOf(Number)) { ^obj.asString };
        if(obj.isKindOf(String)) { ^obj.quote };
        if(obj.isKindOf(Symbol)) { ^obj.asString.quote };
        if(obj.isKindOf(SequenceableCollection)) { ^JSON.listToJSON(obj) };
        if(obj.isKindOf(Association)) { ^JSON.pairToJSON(obj)};

        ^nil;
    }

    *pairToJSON { |pair|
        ^"{" ++ pair.key.asString ++ ":" ++ JSON.toJSON(pair.value) ++ "}";
    }

    *dictToJSON { |dict|
        var str = "{";
        var new_lst = List.new;
        dict.keysValuesDo { |k, v|
            // k.class.postln;
            new_lst.add(JSON.toJSON(k) ++ ":" + JSON.toJSON(v));
        };

        str = str ++ new_lst.join(",");

        str = str ++ "}";
        ^str;
    }

    *listToJSON { |lst|
        var str = "[";
        var new_lst = List.new;
        lst.do { |v|
            new_lst.add(JSON.toJSON(v));
        };
        str = str ++ new_lst.join(",") ++ "]";
        ^str;
    }
}

+String{
	urlEncode{
		var str="";
		this.do({|c|
			if(c.isAlphaNum || "_.-/~".contains(c.asString))
			{str = str++c}
			{str=str++"%"++c.ascii.asHexString(2)}
		})
		^str;
	}
}