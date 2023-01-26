let systemInfo = 
    let out = {
        AST.clid="System.out";
        AST.clname="out";
        AST.classScope=Object.objectInfo;
        AST.clmodifiers=[Public];
        AST.cparent = {tpath=[];tid="Object"} ;
        AST.cattributes = [];
        AST.cinits = [];
        AST.cconsts = [];
        AST.cmethods = [

            {
                AST.mmodifiers = [Public];
                AST.mname = "println";
                AST.mreturntype = Type.Void;
                AST.margstype = [];
                AST.mthrows = [];
                AST.mbody = [];
                AST.mloc = Location.none;
                AST.msemi = false;
            };
            {
                AST.mmodifiers = [Public];
                AST.mname = "println";
                AST.mreturntype = Type.Void;
                AST.margstype = [ 
                    {
                        final = false;
                        vararg = false;
                        ptype = Ref {Type.tpath=[];Type.tid="String"};
                        pident = "str";
                    }
                ];
                AST.mthrows = [];
                AST.mbody = [];
                AST.mloc = Location.none;
                AST.msemi = false;
            };
            {
                AST.mmodifiers = [Public];
                AST.mname = "println";
                AST.mreturntype = Type.Void;
                AST.margstype = [ 
                    {
                        final = false;
                        vararg = false;
                        ptype = Ref {Type.tpath=[];Type.tid="Object"};
                        pident = "str";
                    }
                ];
                AST.mthrows = [];
                AST.mbody = [];
                AST.mloc = Location.none;
                AST.msemi = false;
            };
            {
                AST.mmodifiers = [Public];
                AST.mname = "println";
                AST.mreturntype = Type.Void;
                AST.margstype = [ 
                    {
                        final = false;
                        vararg = false;
                        ptype = Array (Primitive Type.Char,1);
                        pident = "str";
                    }
                ];
                AST.mthrows = [];
                AST.mbody = [];
                AST.mloc = Location.none;
                AST.msemi = false;
            };
            {
                AST.mmodifiers = [Public];
                AST.mname = "println";
                AST.mreturntype = Type.Void;
                AST.margstype = [ 
                    {
                        final = false;
                        vararg = false;
                        ptype = Primitive Type.Boolean;
                        pident = "str";
                    }
                ];
                AST.mthrows = [];
                AST.mbody = [];
                AST.mloc = Location.none;
                AST.msemi = false;
            };
            {
                AST.mmodifiers = [Public];
                AST.mname = "println";
                AST.mreturntype = Type.Void;
                AST.margstype = [ 
                    {
                        final = false;
                        vararg = false;
                        ptype = Primitive Type.Char;
                        pident = "str";
                    }
                ];
                AST.mthrows = [];
                AST.mbody = [];
                AST.mloc = Location.none;
                AST.msemi = false;
            };
            {
                AST.mmodifiers = [Public];
                AST.mname = "println";
                AST.mreturntype = Type.Void;
                AST.margstype = [ 
                    {
                        final = false;
                        vararg = false;
                        ptype = Primitive Type.Byte;
                        pident = "str";
                    }
                ];
                AST.mthrows = [];
                AST.mbody = [];
                AST.mloc = Location.none;
                AST.msemi = false;
            };
            {
                AST.mmodifiers = [Public];
                AST.mname = "println";
                AST.mreturntype = Type.Void;
                AST.margstype = [ 
                    {
                        final = false;
                        vararg = false;
                        ptype = Primitive Type.Short;
                        pident = "str";
                    }
                ];
                AST.mthrows = [];
                AST.mbody = [];
                AST.mloc = Location.none;
                AST.msemi = false;
            };
            {
                AST.mmodifiers = [Public];
                AST.mname = "println";
                AST.mreturntype = Type.Void;
                AST.margstype = [ 
                    {
                        final = false;
                        vararg = false;
                        ptype = Primitive Type.Int;
                        pident = "str";
                    }
                ];
                AST.mthrows = [];
                AST.mbody = [];
                AST.mloc = Location.none;
                AST.msemi = false;
            };
            {
                AST.mmodifiers = [Public];
                AST.mname = "println";
                AST.mreturntype = Type.Void;
                AST.margstype = [ 
                    {
                        final = false;
                        vararg = false;
                        ptype = Primitive Type.Long;
                        pident = "str";
                    }
                ];
                AST.mthrows = [];
                AST.mbody = [];
                AST.mloc = Location.none;
                AST.msemi = false;
            };
            {
                AST.mmodifiers = [Public];
                AST.mname = "println";
                AST.mreturntype = Type.Void;
                AST.margstype = [ 
                    {
                        final = false;
                        vararg = false;
                        ptype = Primitive Type.Float;
                        pident = "str";
                    }
                ];
                AST.mthrows = [];
                AST.mbody = [];
                AST.mloc = Location.none;
                AST.msemi = false;
            };
            {
                AST.mmodifiers = [Public];
                AST.mname = "println";
                AST.mreturntype = Type.Void;
                AST.margstype = [ 
                    {
                        final = false;
                        vararg = false;
                        ptype = Primitive Type.Double;
                        pident = "str";
                    }
                ];
                AST.mthrows = [];
                AST.mbody = [];
                AST.mloc = Location.none;
                AST.msemi = false;
            };
        ];
        AST.ctypes = [];
        AST.cloc = Location.none;
    } in
    let tmp = {
		AST.clid="System";
    	AST.clname="System";
    	AST.classScope=[];
    	AST.clmodifiers=[Public];
    	AST.cparent = {tpath=[];tid="Object"} ;
    	AST.cattributes = [];
    	AST.cinits = [];
    	AST.cconsts = [];
    	AST.cmethods = [];
    	AST.ctypes = [{
            AST.modifiers=[];
            AST.id="out";
            AST.info= AST.Class out
        }];
    	AST.cloc = Location.none;
    } in tmp.classScope<-tmp::out::Object.objectInfo;tmp

