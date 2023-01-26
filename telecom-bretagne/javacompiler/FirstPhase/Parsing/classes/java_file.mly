%{
open Ast
%}

%start javaFile
%start <abstractSyntaxTree> javaFile

%%
javaFile:
	| f=javaCompFile { JFILE f }
	
javaCompFile:
	| p=option(package) i=option(imports) c=option(file_content_l) EOF { 
			let a,p = match p with | None -> [],[] | Some (a,p) -> (
				match a with
				| None -> [],p
				| Some a -> a,p) in
			let i = match i with | None -> [] | Some i -> i in
			let c = match c with | None -> [] | Some c -> c in
				{fannotations=a; fPackage=p; fImports=i; fContent=c;}
		}

package:
	| a=option(annotations) PACKAGE p=pack_name SEMI { a,p }

annotations:
	| a=Annotation { a::[] }
	| a=Annotation l=annotations { a::l }

pack_name:
	| i=IDENTIFIER { i::[] }
	| i=IDENTIFIER DOT p=pack_name { i::p }

pack_import:
	| i=IDENTIFIER { i::[] }
	| i=IDENTIFIER DOT MUL { i::[] }
	| i=IDENTIFIER DOT p=pack_import { i::p }

imports:
	| i=import { i::[] }
	| i=import l=imports { i::l }

import:
	| IMPORT s=option(STATIC) p=pack_import SEMI { 
		match s with | None -> {impStatic=false; impPack=p; impAll=false}
					 | Some s -> {impStatic=true; impPack=p; impAll=false} }

file_content_l:
	| c=file_content { c::[] }
	| l=file_content_l c=file_content { l@[c] }

file_content:
	| c=j_class_plain option(semiColons) { F_Class c }
	| i=j_interface option(semiColons) { F_Interface i }
	| m=modifiers c=j_class_plain option(semiColons) {
			match c with 
			| JavClass c -> c.cmodifiers <- m ;F_Class  (JavClass c) 
			| JavEnum e -> e.emodifiers <- m ;F_Class  (JavEnum e) 
		}
	| m=modifiers i=j_interface option(semiColons) {
			let i = match i with 
			| JI_IN i-> i.imodifiers<-m; JI_IN i 
			| JI_AN i -> i.iaModifiers <- m; JI_AN i in F_Interface i 
		}
