%{
	
%}

%%

%public AnnotationTypeDeclarations:
	|ANOTINT i=IDENTIFIER b=AnnotationTypeBody { {iaModifiers=[]; iaName=i; aibody=b} }


AnnotationTypeBody:
	| LCURL RCURL { [] }
	| LCURL a=AnnotationTypeElementDeclarations RCURL {a}

AnnotationTypeElementDeclarations:
	| e=AnnotationTypeElementDeclaration {e::[]}
	| e=AnnotationTypeElementDeclaration es=AnnotationTypeElementDeclarations {e::es}

AnnotationTypeElementDeclaration:
	| m=modifiers t=tmp {
		let match_inter var m =
			match var with
			|JI_IN i ->  i.imodifiers<- m
			|JI_AN i -> i.iaModifiers<-m
		in 
		let match_class var m = 
		match var with
			| JavClass c -> c.cmodifiers<-m
			| JavEnum e -> e.emodifiers<-m in
		match t with 
		| ATED_Basic b -> b.atedModifs<-m;t
		| ATED_Inter i -> match_inter i m;t
		| ATED_Class c -> match_class c m;t
		| ATED_Declar d -> d.attrmodifiers<-m;t
		| ATED_None -> t

	}
	| t=tmp {t}
	| SEMI {ATED_None} 

tmp:
	| t=allTypes i=IDENTIFIER DEFAULT e=ElementValue SEMI  { ATED_Basic {atedModifs=[];  atedType=t;atedName=i; default=Some e } }
	| t=allTypes i=IDENTIFIER SEMI  { ATED_Basic {atedModifs=[];  atedType=t;atedName=i; default=None } }
	| t=fieldVariableDeclaration { ATED_Declar t } 
	| t=type_params_defin ce=j_class_plain { match ce with | JavClass c -> c.ctparam<-t; ATED_Class ce | JavEnum e -> ATED_Class ce}
	| e=j_class_plain {ATED_Class e}
	| e=j_interface { ATED_Inter e}


%public Annotation:
	| ANOT q=qualifiedName LPAR e=ElementValuePairs RPAR {  {aName= T_Qualified q;aElemPairs=e}  }
	| ANOT q=qualifiedName LPAR e=ElementValue RPAR {   {aName= T_Qualified q;aElemPairs=[{evpId="value" ; evpValue=e}] } }
	| ANOT q=qualifiedName LPAR RPAR { {aName= T_Qualified q;aElemPairs=[]}  }
	| ANOT q=qualifiedName {  {aName=T_Qualified q;aElemPairs=[]} }

ElementValuePairs:
	| e=ElementValuePair {e::[]}
	| e=ElementValuePair es=ElementValuePairs {e::es} 

ElementValuePair:
	| i=IDENTIFIER ASSIGN e=ElementValue { {evpId=i ; evpValue=e} }

ElementValue:
	| c=conditionalExpression {EV_Ex c}
	| a=Annotation {EV_Annot a}
	| e= ElementValueArrayInitializer {EV_Array e} 

ElementValueArrayInitializer:
	| LCURL RCURL { [] }
	| LCURL e=ElementValues option(COMM) RCURL { e }

ElementValues:
	| e=ElementValue {e::[]}
	| es=ElementValues COMM e=ElementValue {es@[e]}

%%