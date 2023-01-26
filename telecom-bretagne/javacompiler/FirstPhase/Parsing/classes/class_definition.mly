%{
	open Ast
%}

%start javaClass
%start <abstractSyntaxTree> javaClass

%%
javaClass:
	| c=j_class option(semiColons) EOF { JCLASS c } /* for testing */

%public j_class:
	| modif=option(modifiers) CLASS id=IDENTIFIER 
		tp=option(type_params_defin) sup=option(super) 
		interf=option(interfaces) bod=class_body { 
			let modif = match modif with | None -> [] | Some m -> m in
			let tp = match tp with | None -> [] | Some tp -> tp in
			let sup = match sup with | None -> C_Object | Some sup -> sup in
			let interf = match interf with | None -> [] | Some interf -> interf in
				JavClass ({cmodifiers=modif;
				cidentifier=id;
				ctparam=tp;
				cparent=sup;
				cinterfaces=interf;
				cbody=bod})
		}
	| modif=option(modifiers) ENUM id=IDENTIFIER
		interf=option(interfaces) LCURL bod=enum_body RCURL { 
			let modif = match modif with | None -> [] | Some m -> m in
			let interf = match interf with | None -> [] | Some interf -> interf in
				JavEnum ({emodifiers=modif;
				eidentifier=id;
				einterfaces=interf;
				ebody=bod})
		}

enum_body:
	| c=option(EnumConstants) d=option(EnumBodyDeclarations) { 
		let f= match c with 
		| Some c -> c 
		| None -> [] in 
			let g = match d with 
			| Some d ->d 
			| None -> [] in 
				(f,g) 
	}

EnumConstants:
	| c=EnumConstant { c::[] }
	| c=EnumConstant COMM l=EnumConstants { c::l }

EnumConstant:
	| a=option(Annotation) id=IDENTIFIER arg=option(args) /*option(class_body)*/ { 
		{ecAnnotation=a;ecIdentifier=id;ecArguments=arg}
	}

args:
	| LPAR arg=option(argumentList) RPAR { match arg with | None -> [] | Some arg -> arg }

EnumBodyDeclarations:
	| semiColons i=option(inside_class_l) { match i with |Some i-> i |None ->[]}


%public super:
	| EXTENDS id=IDENTIFIER typ=option(type_params_defin) {
		match typ with | None -> C_Parent(id,None) | Some typ -> C_Parent(id,Some typ)
	}

interfaces:
	| IMPLEMENTS i=interface_list { i }

interface_list:
	| id=IDENTIFIER { id::[] }
	| id=IDENTIFIER COMM l=interface_list { id::l }

%public class_body:
	| SEMI { [IC_Semi] }
	| LCURL bod=inside_class_l RCURL { bod }
	| LCURL RCURL { [IC_Empty] }

%public inside_class_l:
	| i=inside_class { i::[] }
	| i=inside_class l=inside_class_l { i::l }

inside_class:
	| 			  cma=class_method_or_attribute { cma }
	| m=modifiers cma=class_method_or_attribute { 
		match cma with 
		| IC_Method me -> me.jmmodifiers<-m; cma 
		| IC_Class c -> 
			(match c with 
			| JavClass c -> c.cmodifiers<-m; cma
			| JavEnum e -> e.emodifiers<-m; cma)
		| IC_Attribute a -> a.attrmodifiers<-m; cma 
		| _ -> cma }
	| STATIC b=block { IC_Static(b) }
/*	| m=modifiers 	c=constructor { c.constrmodifiers<-m; IC_Constructor c }
	| 				c=constructor { IC_Constructor c } */
	

class_method_or_attribute:
	| m=method_or_attribute { m }
	| c=j_class_plain { IC_Class c }

method_or_attribute:
	| t=type_params_defin j=javaMethod_plain_return { j.jmtparam<-t;IC_Method j }
	| VOID j=javaMethod_plain { IC_Method j }
	| j=javaMethod_plain { IC_Constructor j }
	| fvd=fieldVariableDeclaration { IC_Attribute fvd }
	| t=allTypes j=javaMethod_plain { j.jmrtype<-RT_Type t; IC_Method j }
	| i=j_interface_plain { IC_Interface (JI_IN i) }



%public j_class_plain:
	| CLASS id=IDENTIFIER 
		tp=option(type_params_defin) sup=option(super) 
		interf=option(interfaces) bod=class_body {
			let tp = match tp with | None -> [] | Some tp -> tp in
			let sup = match sup with | None -> C_Object | Some sup -> sup in
			let interf = match interf with | None -> [] | Some interf -> interf in
				JavClass ({cmodifiers=[];
				cidentifier=id;
				ctparam=tp;
				cparent=sup;
				cinterfaces=interf;
				cbody=bod})
		}
	| ENUM id=IDENTIFIER
		interf=option(interfaces) LCURL bod=enum_body RCURL {
			let interf = match interf with | None -> [] | Some interf -> interf in
				JavEnum ({emodifiers=[];
				eidentifier=id;
				einterfaces=interf;
				ebody=bod})
		}
/*
constructor:
 	| tp=option(type_params_defin) m=MethodDeclarator t=option(Throws) b=block option(semiColons) {
			let tp = match tp with | None -> [] | Some tp -> tp in
			let t = match t with | None -> [] | Some t -> t in
				{constrmodifiers=[];
				constrdeclarator=m;
				constrtparam=tp;
				constrthrows=t;
				constrbody=b} 
		}
 	/*this IDENTIFIER must be the simple name of the class*/ 
%%
