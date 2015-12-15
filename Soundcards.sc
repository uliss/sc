Saffire {
    *gui { |bufsize = 256|
        Server.default = Server.new("FocusWrite Saffire", NetAddr("127.0.0.1", 58009),
            ServerOptions.new.device_("Saffire").memSize_((2**10) * 100).hardwareBufferSize_(bufsize).numOutputBusChannels_(6).numInputBusChannels_(4)).makeWindow;
        ^Server.default;
    }
}

MBox {
    *gui {
        Server.default = Server.new("MBox Pro", NetAddr("127.0.0.1", 58010),
            ServerOptions.new.device_("MBox Pro")).makeWindow;
        ^Server.default;
    }
}

BuiltIn {
    *gui {
        var srv = Server.new("Built-In macbook", NetAddr("127.0.0.1", 58011),
            ServerOptions.new.device_("BuiltIn").memSize_((2**10) * 100));
        srv.makeWindow;
        Server.default = srv;
        ^srv;
    }
}
