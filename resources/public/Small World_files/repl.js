// Compiled by ClojureScript 1.10.520 {}
goog.provide('cljs.repl');
goog.require('cljs.core');
goog.require('cljs.spec.alpha');
goog.require('goog.string');
goog.require('goog.string.format');
cljs.repl.print_doc = (function cljs$repl$print_doc(p__37140){
var map__37141 = p__37140;
var map__37141__$1 = (((((!((map__37141 == null))))?(((((map__37141.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37141.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37141):map__37141);
var m = map__37141__$1;
var n = cljs.core.get.call(null,map__37141__$1,new cljs.core.Keyword(null,"ns","ns",441598760));
var nm = cljs.core.get.call(null,map__37141__$1,new cljs.core.Keyword(null,"name","name",1843675177));
cljs.core.println.call(null,"-------------------------");

cljs.core.println.call(null,(function (){var or__4131__auto__ = new cljs.core.Keyword(null,"spec","spec",347520401).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return [(function (){var temp__5753__auto__ = new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5753__auto__)){
var ns = temp__5753__auto__;
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(ns),"/"].join('');
} else {
return null;
}
})(),cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m))].join('');
}
})());

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Protocol");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m))){
var seq__37143_37175 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m));
var chunk__37144_37176 = null;
var count__37145_37177 = (0);
var i__37146_37178 = (0);
while(true){
if((i__37146_37178 < count__37145_37177)){
var f_37179 = cljs.core._nth.call(null,chunk__37144_37176,i__37146_37178);
cljs.core.println.call(null,"  ",f_37179);


var G__37180 = seq__37143_37175;
var G__37181 = chunk__37144_37176;
var G__37182 = count__37145_37177;
var G__37183 = (i__37146_37178 + (1));
seq__37143_37175 = G__37180;
chunk__37144_37176 = G__37181;
count__37145_37177 = G__37182;
i__37146_37178 = G__37183;
continue;
} else {
var temp__5753__auto___37184 = cljs.core.seq.call(null,seq__37143_37175);
if(temp__5753__auto___37184){
var seq__37143_37185__$1 = temp__5753__auto___37184;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__37143_37185__$1)){
var c__4550__auto___37186 = cljs.core.chunk_first.call(null,seq__37143_37185__$1);
var G__37187 = cljs.core.chunk_rest.call(null,seq__37143_37185__$1);
var G__37188 = c__4550__auto___37186;
var G__37189 = cljs.core.count.call(null,c__4550__auto___37186);
var G__37190 = (0);
seq__37143_37175 = G__37187;
chunk__37144_37176 = G__37188;
count__37145_37177 = G__37189;
i__37146_37178 = G__37190;
continue;
} else {
var f_37191 = cljs.core.first.call(null,seq__37143_37185__$1);
cljs.core.println.call(null,"  ",f_37191);


var G__37192 = cljs.core.next.call(null,seq__37143_37185__$1);
var G__37193 = null;
var G__37194 = (0);
var G__37195 = (0);
seq__37143_37175 = G__37192;
chunk__37144_37176 = G__37193;
count__37145_37177 = G__37194;
i__37146_37178 = G__37195;
continue;
}
} else {
}
}
break;
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m))){
var arglists_37196 = new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_((function (){var or__4131__auto__ = new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m);
}
})())){
cljs.core.prn.call(null,arglists_37196);
} else {
cljs.core.prn.call(null,((cljs.core._EQ_.call(null,new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.first.call(null,arglists_37196)))?cljs.core.second.call(null,arglists_37196):arglists_37196));
}
} else {
}
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"special-form","special-form",-1326536374).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Special Form");

cljs.core.println.call(null," ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.contains_QMARK_.call(null,m,new cljs.core.Keyword(null,"url","url",276297046))){
if(cljs.core.truth_(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))){
return cljs.core.println.call(null,["\n  Please see http://clojure.org/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))].join(''));
} else {
return null;
}
} else {
return cljs.core.println.call(null,["\n  Please see http://clojure.org/special_forms#",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m))].join(''));
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Macro");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"spec","spec",347520401).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Spec");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"REPL Special Function");
} else {
}

cljs.core.println.call(null," ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
var seq__37147_37197 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"methods","methods",453930866).cljs$core$IFn$_invoke$arity$1(m));
var chunk__37148_37198 = null;
var count__37149_37199 = (0);
var i__37150_37200 = (0);
while(true){
if((i__37150_37200 < count__37149_37199)){
var vec__37161_37201 = cljs.core._nth.call(null,chunk__37148_37198,i__37150_37200);
var name_37202 = cljs.core.nth.call(null,vec__37161_37201,(0),null);
var map__37164_37203 = cljs.core.nth.call(null,vec__37161_37201,(1),null);
var map__37164_37204__$1 = (((((!((map__37164_37203 == null))))?(((((map__37164_37203.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37164_37203.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37164_37203):map__37164_37203);
var doc_37205 = cljs.core.get.call(null,map__37164_37204__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists_37206 = cljs.core.get.call(null,map__37164_37204__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println.call(null);

cljs.core.println.call(null," ",name_37202);

cljs.core.println.call(null," ",arglists_37206);

if(cljs.core.truth_(doc_37205)){
cljs.core.println.call(null," ",doc_37205);
} else {
}


var G__37207 = seq__37147_37197;
var G__37208 = chunk__37148_37198;
var G__37209 = count__37149_37199;
var G__37210 = (i__37150_37200 + (1));
seq__37147_37197 = G__37207;
chunk__37148_37198 = G__37208;
count__37149_37199 = G__37209;
i__37150_37200 = G__37210;
continue;
} else {
var temp__5753__auto___37211 = cljs.core.seq.call(null,seq__37147_37197);
if(temp__5753__auto___37211){
var seq__37147_37212__$1 = temp__5753__auto___37211;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__37147_37212__$1)){
var c__4550__auto___37213 = cljs.core.chunk_first.call(null,seq__37147_37212__$1);
var G__37214 = cljs.core.chunk_rest.call(null,seq__37147_37212__$1);
var G__37215 = c__4550__auto___37213;
var G__37216 = cljs.core.count.call(null,c__4550__auto___37213);
var G__37217 = (0);
seq__37147_37197 = G__37214;
chunk__37148_37198 = G__37215;
count__37149_37199 = G__37216;
i__37150_37200 = G__37217;
continue;
} else {
var vec__37166_37218 = cljs.core.first.call(null,seq__37147_37212__$1);
var name_37219 = cljs.core.nth.call(null,vec__37166_37218,(0),null);
var map__37169_37220 = cljs.core.nth.call(null,vec__37166_37218,(1),null);
var map__37169_37221__$1 = (((((!((map__37169_37220 == null))))?(((((map__37169_37220.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37169_37220.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37169_37220):map__37169_37220);
var doc_37222 = cljs.core.get.call(null,map__37169_37221__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists_37223 = cljs.core.get.call(null,map__37169_37221__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println.call(null);

cljs.core.println.call(null," ",name_37219);

cljs.core.println.call(null," ",arglists_37223);

if(cljs.core.truth_(doc_37222)){
cljs.core.println.call(null," ",doc_37222);
} else {
}


var G__37224 = cljs.core.next.call(null,seq__37147_37212__$1);
var G__37225 = null;
var G__37226 = (0);
var G__37227 = (0);
seq__37147_37197 = G__37224;
chunk__37148_37198 = G__37225;
count__37149_37199 = G__37226;
i__37150_37200 = G__37227;
continue;
}
} else {
}
}
break;
}
} else {
}

if(cljs.core.truth_(n)){
var temp__5753__auto__ = cljs.spec.alpha.get_spec.call(null,cljs.core.symbol.call(null,cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.ns_name.call(null,n)),cljs.core.name.call(null,nm)));
if(cljs.core.truth_(temp__5753__auto__)){
var fnspec = temp__5753__auto__;
cljs.core.print.call(null,"Spec");

var seq__37171 = cljs.core.seq.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"args","args",1315556576),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Keyword(null,"fn","fn",-1175266204)], null));
var chunk__37172 = null;
var count__37173 = (0);
var i__37174 = (0);
while(true){
if((i__37174 < count__37173)){
var role = cljs.core._nth.call(null,chunk__37172,i__37174);
var temp__5753__auto___37228__$1 = cljs.core.get.call(null,fnspec,role);
if(cljs.core.truth_(temp__5753__auto___37228__$1)){
var spec_37229 = temp__5753__auto___37228__$1;
cljs.core.print.call(null,["\n ",cljs.core.name.call(null,role),":"].join(''),cljs.spec.alpha.describe.call(null,spec_37229));
} else {
}


var G__37230 = seq__37171;
var G__37231 = chunk__37172;
var G__37232 = count__37173;
var G__37233 = (i__37174 + (1));
seq__37171 = G__37230;
chunk__37172 = G__37231;
count__37173 = G__37232;
i__37174 = G__37233;
continue;
} else {
var temp__5753__auto____$1 = cljs.core.seq.call(null,seq__37171);
if(temp__5753__auto____$1){
var seq__37171__$1 = temp__5753__auto____$1;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__37171__$1)){
var c__4550__auto__ = cljs.core.chunk_first.call(null,seq__37171__$1);
var G__37234 = cljs.core.chunk_rest.call(null,seq__37171__$1);
var G__37235 = c__4550__auto__;
var G__37236 = cljs.core.count.call(null,c__4550__auto__);
var G__37237 = (0);
seq__37171 = G__37234;
chunk__37172 = G__37235;
count__37173 = G__37236;
i__37174 = G__37237;
continue;
} else {
var role = cljs.core.first.call(null,seq__37171__$1);
var temp__5753__auto___37238__$2 = cljs.core.get.call(null,fnspec,role);
if(cljs.core.truth_(temp__5753__auto___37238__$2)){
var spec_37239 = temp__5753__auto___37238__$2;
cljs.core.print.call(null,["\n ",cljs.core.name.call(null,role),":"].join(''),cljs.spec.alpha.describe.call(null,spec_37239));
} else {
}


var G__37240 = cljs.core.next.call(null,seq__37171__$1);
var G__37241 = null;
var G__37242 = (0);
var G__37243 = (0);
seq__37171 = G__37240;
chunk__37172 = G__37241;
count__37173 = G__37242;
i__37174 = G__37243;
continue;
}
} else {
return null;
}
}
break;
}
} else {
return null;
}
} else {
return null;
}
}
});
/**
 * Constructs a data representation for a Error with keys:
 *  :cause - root cause message
 *  :phase - error phase
 *  :via - cause chain, with cause keys:
 *           :type - exception class symbol
 *           :message - exception message
 *           :data - ex-data
 *           :at - top stack element
 *  :trace - root cause stack elements
 */
cljs.repl.Error__GT_map = (function cljs$repl$Error__GT_map(o){
var base = (function (t){
return cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),(((t instanceof cljs.core.ExceptionInfo))?new cljs.core.Symbol(null,"ExceptionInfo","ExceptionInfo",294935087,null):(((t instanceof EvalError))?new cljs.core.Symbol("js","EvalError","js/EvalError",1793498501,null):(((t instanceof RangeError))?new cljs.core.Symbol("js","RangeError","js/RangeError",1703848089,null):(((t instanceof ReferenceError))?new cljs.core.Symbol("js","ReferenceError","js/ReferenceError",-198403224,null):(((t instanceof SyntaxError))?new cljs.core.Symbol("js","SyntaxError","js/SyntaxError",-1527651665,null):(((t instanceof URIError))?new cljs.core.Symbol("js","URIError","js/URIError",505061350,null):(((t instanceof Error))?new cljs.core.Symbol("js","Error","js/Error",-1692659266,null):null
)))))))], null),(function (){var temp__5753__auto__ = cljs.core.ex_message.call(null,t);
if(cljs.core.truth_(temp__5753__auto__)){
var msg = temp__5753__auto__;
return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"message","message",-406056002),msg], null);
} else {
return null;
}
})(),(function (){var temp__5753__auto__ = cljs.core.ex_data.call(null,t);
if(cljs.core.truth_(temp__5753__auto__)){
var ed = temp__5753__auto__;
return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"data","data",-232669377),ed], null);
} else {
return null;
}
})());
});
var via = (function (){var via = cljs.core.PersistentVector.EMPTY;
var t = o;
while(true){
if(cljs.core.truth_(t)){
var G__37244 = cljs.core.conj.call(null,via,t);
var G__37245 = cljs.core.ex_cause.call(null,t);
via = G__37244;
t = G__37245;
continue;
} else {
return via;
}
break;
}
})();
var root = cljs.core.peek.call(null,via);
return cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"via","via",-1904457336),cljs.core.vec.call(null,cljs.core.map.call(null,base,via)),new cljs.core.Keyword(null,"trace","trace",-1082747415),null], null),(function (){var temp__5753__auto__ = cljs.core.ex_message.call(null,root);
if(cljs.core.truth_(temp__5753__auto__)){
var root_msg = temp__5753__auto__;
return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"cause","cause",231901252),root_msg], null);
} else {
return null;
}
})(),(function (){var temp__5753__auto__ = cljs.core.ex_data.call(null,root);
if(cljs.core.truth_(temp__5753__auto__)){
var data = temp__5753__auto__;
return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"data","data",-232669377),data], null);
} else {
return null;
}
})(),(function (){var temp__5753__auto__ = new cljs.core.Keyword("clojure.error","phase","clojure.error/phase",275140358).cljs$core$IFn$_invoke$arity$1(cljs.core.ex_data.call(null,o));
if(cljs.core.truth_(temp__5753__auto__)){
var phase = temp__5753__auto__;
return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"phase","phase",575722892),phase], null);
} else {
return null;
}
})());
});
/**
 * Returns an analysis of the phase, error, cause, and location of an error that occurred
 *   based on Throwable data, as returned by Throwable->map. All attributes other than phase
 *   are optional:
 *  :clojure.error/phase - keyword phase indicator, one of:
 *    :read-source :compile-syntax-check :compilation :macro-syntax-check :macroexpansion
 *    :execution :read-eval-result :print-eval-result
 *  :clojure.error/source - file name (no path)
 *  :clojure.error/line - integer line number
 *  :clojure.error/column - integer column number
 *  :clojure.error/symbol - symbol being expanded/compiled/invoked
 *  :clojure.error/class - cause exception class symbol
 *  :clojure.error/cause - cause exception message
 *  :clojure.error/spec - explain-data for spec error
 */
cljs.repl.ex_triage = (function cljs$repl$ex_triage(datafied_throwable){
var map__37248 = datafied_throwable;
var map__37248__$1 = (((((!((map__37248 == null))))?(((((map__37248.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37248.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37248):map__37248);
var via = cljs.core.get.call(null,map__37248__$1,new cljs.core.Keyword(null,"via","via",-1904457336));
var trace = cljs.core.get.call(null,map__37248__$1,new cljs.core.Keyword(null,"trace","trace",-1082747415));
var phase = cljs.core.get.call(null,map__37248__$1,new cljs.core.Keyword(null,"phase","phase",575722892),new cljs.core.Keyword(null,"execution","execution",253283524));
var map__37249 = cljs.core.last.call(null,via);
var map__37249__$1 = (((((!((map__37249 == null))))?(((((map__37249.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37249.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37249):map__37249);
var type = cljs.core.get.call(null,map__37249__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var message = cljs.core.get.call(null,map__37249__$1,new cljs.core.Keyword(null,"message","message",-406056002));
var data = cljs.core.get.call(null,map__37249__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var map__37250 = data;
var map__37250__$1 = (((((!((map__37250 == null))))?(((((map__37250.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37250.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37250):map__37250);
var problems = cljs.core.get.call(null,map__37250__$1,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814));
var fn = cljs.core.get.call(null,map__37250__$1,new cljs.core.Keyword("cljs.spec.alpha","fn","cljs.spec.alpha/fn",408600443));
var caller = cljs.core.get.call(null,map__37250__$1,new cljs.core.Keyword("cljs.spec.test.alpha","caller","cljs.spec.test.alpha/caller",-398302390));
var map__37251 = new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(cljs.core.first.call(null,via));
var map__37251__$1 = (((((!((map__37251 == null))))?(((((map__37251.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37251.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37251):map__37251);
var top_data = map__37251__$1;
var source = cljs.core.get.call(null,map__37251__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397));
return cljs.core.assoc.call(null,(function (){var G__37256 = phase;
var G__37256__$1 = (((G__37256 instanceof cljs.core.Keyword))?G__37256.fqn:null);
switch (G__37256__$1) {
case "read-source":
var map__37257 = data;
var map__37257__$1 = (((((!((map__37257 == null))))?(((((map__37257.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37257.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37257):map__37257);
var line = cljs.core.get.call(null,map__37257__$1,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471));
var column = cljs.core.get.call(null,map__37257__$1,new cljs.core.Keyword("clojure.error","column","clojure.error/column",304721553));
var G__37259 = cljs.core.merge.call(null,new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(cljs.core.second.call(null,via)),top_data);
var G__37259__$1 = (cljs.core.truth_(source)?cljs.core.assoc.call(null,G__37259,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),source):G__37259);
var G__37259__$2 = (cljs.core.truth_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["NO_SOURCE_PATH",null,"NO_SOURCE_FILE",null], null), null).call(null,source))?cljs.core.dissoc.call(null,G__37259__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397)):G__37259__$1);
if(cljs.core.truth_(message)){
return cljs.core.assoc.call(null,G__37259__$2,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message);
} else {
return G__37259__$2;
}

break;
case "compile-syntax-check":
case "compilation":
case "macro-syntax-check":
case "macroexpansion":
var G__37260 = top_data;
var G__37260__$1 = (cljs.core.truth_(source)?cljs.core.assoc.call(null,G__37260,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),source):G__37260);
var G__37260__$2 = (cljs.core.truth_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["NO_SOURCE_PATH",null,"NO_SOURCE_FILE",null], null), null).call(null,source))?cljs.core.dissoc.call(null,G__37260__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397)):G__37260__$1);
var G__37260__$3 = (cljs.core.truth_(type)?cljs.core.assoc.call(null,G__37260__$2,new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890),type):G__37260__$2);
var G__37260__$4 = (cljs.core.truth_(message)?cljs.core.assoc.call(null,G__37260__$3,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message):G__37260__$3);
if(cljs.core.truth_(problems)){
return cljs.core.assoc.call(null,G__37260__$4,new cljs.core.Keyword("clojure.error","spec","clojure.error/spec",2055032595),data);
} else {
return G__37260__$4;
}

break;
case "read-eval-result":
case "print-eval-result":
var vec__37261 = cljs.core.first.call(null,trace);
var source__$1 = cljs.core.nth.call(null,vec__37261,(0),null);
var method = cljs.core.nth.call(null,vec__37261,(1),null);
var file = cljs.core.nth.call(null,vec__37261,(2),null);
var line = cljs.core.nth.call(null,vec__37261,(3),null);
var G__37264 = top_data;
var G__37264__$1 = (cljs.core.truth_(line)?cljs.core.assoc.call(null,G__37264,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471),line):G__37264);
var G__37264__$2 = (cljs.core.truth_(file)?cljs.core.assoc.call(null,G__37264__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),file):G__37264__$1);
var G__37264__$3 = (cljs.core.truth_((function (){var and__4120__auto__ = source__$1;
if(cljs.core.truth_(and__4120__auto__)){
return method;
} else {
return and__4120__auto__;
}
})())?cljs.core.assoc.call(null,G__37264__$2,new cljs.core.Keyword("clojure.error","symbol","clojure.error/symbol",1544821994),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[source__$1,method],null))):G__37264__$2);
var G__37264__$4 = (cljs.core.truth_(type)?cljs.core.assoc.call(null,G__37264__$3,new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890),type):G__37264__$3);
if(cljs.core.truth_(message)){
return cljs.core.assoc.call(null,G__37264__$4,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message);
} else {
return G__37264__$4;
}

break;
case "execution":
var vec__37265 = cljs.core.first.call(null,trace);
var source__$1 = cljs.core.nth.call(null,vec__37265,(0),null);
var method = cljs.core.nth.call(null,vec__37265,(1),null);
var file = cljs.core.nth.call(null,vec__37265,(2),null);
var line = cljs.core.nth.call(null,vec__37265,(3),null);
var file__$1 = cljs.core.first.call(null,cljs.core.remove.call(null,((function (vec__37265,source__$1,method,file,line,G__37256,G__37256__$1,map__37248,map__37248__$1,via,trace,phase,map__37249,map__37249__$1,type,message,data,map__37250,map__37250__$1,problems,fn,caller,map__37251,map__37251__$1,top_data,source){
return (function (p1__37247_SHARP_){
var or__4131__auto__ = (p1__37247_SHARP_ == null);
if(or__4131__auto__){
return or__4131__auto__;
} else {
return new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["NO_SOURCE_PATH",null,"NO_SOURCE_FILE",null], null), null).call(null,p1__37247_SHARP_);
}
});})(vec__37265,source__$1,method,file,line,G__37256,G__37256__$1,map__37248,map__37248__$1,via,trace,phase,map__37249,map__37249__$1,type,message,data,map__37250,map__37250__$1,problems,fn,caller,map__37251,map__37251__$1,top_data,source))
,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"file","file",-1269645878).cljs$core$IFn$_invoke$arity$1(caller),file], null)));
var err_line = (function (){var or__4131__auto__ = new cljs.core.Keyword(null,"line","line",212345235).cljs$core$IFn$_invoke$arity$1(caller);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return line;
}
})();
var G__37268 = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890),type], null);
var G__37268__$1 = (cljs.core.truth_(err_line)?cljs.core.assoc.call(null,G__37268,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471),err_line):G__37268);
var G__37268__$2 = (cljs.core.truth_(message)?cljs.core.assoc.call(null,G__37268__$1,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message):G__37268__$1);
var G__37268__$3 = (cljs.core.truth_((function (){var or__4131__auto__ = fn;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
var and__4120__auto__ = source__$1;
if(cljs.core.truth_(and__4120__auto__)){
return method;
} else {
return and__4120__auto__;
}
}
})())?cljs.core.assoc.call(null,G__37268__$2,new cljs.core.Keyword("clojure.error","symbol","clojure.error/symbol",1544821994),(function (){var or__4131__auto__ = fn;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return (new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[source__$1,method],null));
}
})()):G__37268__$2);
var G__37268__$4 = (cljs.core.truth_(file__$1)?cljs.core.assoc.call(null,G__37268__$3,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),file__$1):G__37268__$3);
if(cljs.core.truth_(problems)){
return cljs.core.assoc.call(null,G__37268__$4,new cljs.core.Keyword("clojure.error","spec","clojure.error/spec",2055032595),data);
} else {
return G__37268__$4;
}

break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__37256__$1)].join('')));

}
})(),new cljs.core.Keyword("clojure.error","phase","clojure.error/phase",275140358),phase);
});
/**
 * Returns a string from exception data, as produced by ex-triage.
 *   The first line summarizes the exception phase and location.
 *   The subsequent lines describe the cause.
 */
cljs.repl.ex_str = (function cljs$repl$ex_str(p__37272){
var map__37273 = p__37272;
var map__37273__$1 = (((((!((map__37273 == null))))?(((((map__37273.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37273.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37273):map__37273);
var triage_data = map__37273__$1;
var phase = cljs.core.get.call(null,map__37273__$1,new cljs.core.Keyword("clojure.error","phase","clojure.error/phase",275140358));
var source = cljs.core.get.call(null,map__37273__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397));
var line = cljs.core.get.call(null,map__37273__$1,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471));
var column = cljs.core.get.call(null,map__37273__$1,new cljs.core.Keyword("clojure.error","column","clojure.error/column",304721553));
var symbol = cljs.core.get.call(null,map__37273__$1,new cljs.core.Keyword("clojure.error","symbol","clojure.error/symbol",1544821994));
var class$ = cljs.core.get.call(null,map__37273__$1,new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890));
var cause = cljs.core.get.call(null,map__37273__$1,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742));
var spec = cljs.core.get.call(null,map__37273__$1,new cljs.core.Keyword("clojure.error","spec","clojure.error/spec",2055032595));
var loc = [cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var or__4131__auto__ = source;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return "<cljs repl>";
}
})()),":",cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var or__4131__auto__ = line;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return (1);
}
})()),(cljs.core.truth_(column)?[":",cljs.core.str.cljs$core$IFn$_invoke$arity$1(column)].join(''):"")].join('');
var class_name = cljs.core.name.call(null,(function (){var or__4131__auto__ = class$;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return "";
}
})());
var simple_class = class_name;
var cause_type = ((cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["RuntimeException",null,"Exception",null], null), null),simple_class))?"":[" (",simple_class,")"].join(''));
var format = goog.string.format;
var G__37275 = phase;
var G__37275__$1 = (((G__37275 instanceof cljs.core.Keyword))?G__37275.fqn:null);
switch (G__37275__$1) {
case "read-source":
return format.call(null,"Syntax error reading source at (%s).\n%s\n",loc,cause);

break;
case "macro-syntax-check":
return format.call(null,"Syntax error macroexpanding %sat (%s).\n%s",(cljs.core.truth_(symbol)?[cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)," "].join(''):""),loc,(cljs.core.truth_(spec)?(function (){var sb__4661__auto__ = (new goog.string.StringBuffer());
var _STAR_print_newline_STAR__orig_val__37276_37285 = cljs.core._STAR_print_newline_STAR_;
var _STAR_print_fn_STAR__orig_val__37277_37286 = cljs.core._STAR_print_fn_STAR_;
var _STAR_print_newline_STAR__temp_val__37278_37287 = true;
var _STAR_print_fn_STAR__temp_val__37279_37288 = ((function (_STAR_print_newline_STAR__orig_val__37276_37285,_STAR_print_fn_STAR__orig_val__37277_37286,_STAR_print_newline_STAR__temp_val__37278_37287,sb__4661__auto__,G__37275,G__37275__$1,loc,class_name,simple_class,cause_type,format,map__37273,map__37273__$1,triage_data,phase,source,line,column,symbol,class$,cause,spec){
return (function (x__4662__auto__){
return sb__4661__auto__.append(x__4662__auto__);
});})(_STAR_print_newline_STAR__orig_val__37276_37285,_STAR_print_fn_STAR__orig_val__37277_37286,_STAR_print_newline_STAR__temp_val__37278_37287,sb__4661__auto__,G__37275,G__37275__$1,loc,class_name,simple_class,cause_type,format,map__37273,map__37273__$1,triage_data,phase,source,line,column,symbol,class$,cause,spec))
;
cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__temp_val__37278_37287;

cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__temp_val__37279_37288;

try{cljs.spec.alpha.explain_out.call(null,cljs.core.update.call(null,spec,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814),((function (_STAR_print_newline_STAR__orig_val__37276_37285,_STAR_print_fn_STAR__orig_val__37277_37286,_STAR_print_newline_STAR__temp_val__37278_37287,_STAR_print_fn_STAR__temp_val__37279_37288,sb__4661__auto__,G__37275,G__37275__$1,loc,class_name,simple_class,cause_type,format,map__37273,map__37273__$1,triage_data,phase,source,line,column,symbol,class$,cause,spec){
return (function (probs){
return cljs.core.map.call(null,((function (_STAR_print_newline_STAR__orig_val__37276_37285,_STAR_print_fn_STAR__orig_val__37277_37286,_STAR_print_newline_STAR__temp_val__37278_37287,_STAR_print_fn_STAR__temp_val__37279_37288,sb__4661__auto__,G__37275,G__37275__$1,loc,class_name,simple_class,cause_type,format,map__37273,map__37273__$1,triage_data,phase,source,line,column,symbol,class$,cause,spec){
return (function (p1__37270_SHARP_){
return cljs.core.dissoc.call(null,p1__37270_SHARP_,new cljs.core.Keyword(null,"in","in",-1531184865));
});})(_STAR_print_newline_STAR__orig_val__37276_37285,_STAR_print_fn_STAR__orig_val__37277_37286,_STAR_print_newline_STAR__temp_val__37278_37287,_STAR_print_fn_STAR__temp_val__37279_37288,sb__4661__auto__,G__37275,G__37275__$1,loc,class_name,simple_class,cause_type,format,map__37273,map__37273__$1,triage_data,phase,source,line,column,symbol,class$,cause,spec))
,probs);
});})(_STAR_print_newline_STAR__orig_val__37276_37285,_STAR_print_fn_STAR__orig_val__37277_37286,_STAR_print_newline_STAR__temp_val__37278_37287,_STAR_print_fn_STAR__temp_val__37279_37288,sb__4661__auto__,G__37275,G__37275__$1,loc,class_name,simple_class,cause_type,format,map__37273,map__37273__$1,triage_data,phase,source,line,column,symbol,class$,cause,spec))
)
);
}finally {cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__orig_val__37277_37286;

cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__orig_val__37276_37285;
}
return cljs.core.str.cljs$core$IFn$_invoke$arity$1(sb__4661__auto__);
})():format.call(null,"%s\n",cause)));

break;
case "macroexpansion":
return format.call(null,"Unexpected error%s macroexpanding %sat (%s).\n%s\n",cause_type,(cljs.core.truth_(symbol)?[cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)," "].join(''):""),loc,cause);

break;
case "compile-syntax-check":
return format.call(null,"Syntax error%s compiling %sat (%s).\n%s\n",cause_type,(cljs.core.truth_(symbol)?[cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)," "].join(''):""),loc,cause);

break;
case "compilation":
return format.call(null,"Unexpected error%s compiling %sat (%s).\n%s\n",cause_type,(cljs.core.truth_(symbol)?[cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)," "].join(''):""),loc,cause);

break;
case "read-eval-result":
return format.call(null,"Error reading eval result%s at %s (%s).\n%s\n",cause_type,symbol,loc,cause);

break;
case "print-eval-result":
return format.call(null,"Error printing return value%s at %s (%s).\n%s\n",cause_type,symbol,loc,cause);

break;
case "execution":
if(cljs.core.truth_(spec)){
return format.call(null,"Execution error - invalid arguments to %s at (%s).\n%s",symbol,loc,(function (){var sb__4661__auto__ = (new goog.string.StringBuffer());
var _STAR_print_newline_STAR__orig_val__37280_37289 = cljs.core._STAR_print_newline_STAR_;
var _STAR_print_fn_STAR__orig_val__37281_37290 = cljs.core._STAR_print_fn_STAR_;
var _STAR_print_newline_STAR__temp_val__37282_37291 = true;
var _STAR_print_fn_STAR__temp_val__37283_37292 = ((function (_STAR_print_newline_STAR__orig_val__37280_37289,_STAR_print_fn_STAR__orig_val__37281_37290,_STAR_print_newline_STAR__temp_val__37282_37291,sb__4661__auto__,G__37275,G__37275__$1,loc,class_name,simple_class,cause_type,format,map__37273,map__37273__$1,triage_data,phase,source,line,column,symbol,class$,cause,spec){
return (function (x__4662__auto__){
return sb__4661__auto__.append(x__4662__auto__);
});})(_STAR_print_newline_STAR__orig_val__37280_37289,_STAR_print_fn_STAR__orig_val__37281_37290,_STAR_print_newline_STAR__temp_val__37282_37291,sb__4661__auto__,G__37275,G__37275__$1,loc,class_name,simple_class,cause_type,format,map__37273,map__37273__$1,triage_data,phase,source,line,column,symbol,class$,cause,spec))
;
cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__temp_val__37282_37291;

cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__temp_val__37283_37292;

try{cljs.spec.alpha.explain_out.call(null,cljs.core.update.call(null,spec,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814),((function (_STAR_print_newline_STAR__orig_val__37280_37289,_STAR_print_fn_STAR__orig_val__37281_37290,_STAR_print_newline_STAR__temp_val__37282_37291,_STAR_print_fn_STAR__temp_val__37283_37292,sb__4661__auto__,G__37275,G__37275__$1,loc,class_name,simple_class,cause_type,format,map__37273,map__37273__$1,triage_data,phase,source,line,column,symbol,class$,cause,spec){
return (function (probs){
return cljs.core.map.call(null,((function (_STAR_print_newline_STAR__orig_val__37280_37289,_STAR_print_fn_STAR__orig_val__37281_37290,_STAR_print_newline_STAR__temp_val__37282_37291,_STAR_print_fn_STAR__temp_val__37283_37292,sb__4661__auto__,G__37275,G__37275__$1,loc,class_name,simple_class,cause_type,format,map__37273,map__37273__$1,triage_data,phase,source,line,column,symbol,class$,cause,spec){
return (function (p1__37271_SHARP_){
return cljs.core.dissoc.call(null,p1__37271_SHARP_,new cljs.core.Keyword(null,"in","in",-1531184865));
});})(_STAR_print_newline_STAR__orig_val__37280_37289,_STAR_print_fn_STAR__orig_val__37281_37290,_STAR_print_newline_STAR__temp_val__37282_37291,_STAR_print_fn_STAR__temp_val__37283_37292,sb__4661__auto__,G__37275,G__37275__$1,loc,class_name,simple_class,cause_type,format,map__37273,map__37273__$1,triage_data,phase,source,line,column,symbol,class$,cause,spec))
,probs);
});})(_STAR_print_newline_STAR__orig_val__37280_37289,_STAR_print_fn_STAR__orig_val__37281_37290,_STAR_print_newline_STAR__temp_val__37282_37291,_STAR_print_fn_STAR__temp_val__37283_37292,sb__4661__auto__,G__37275,G__37275__$1,loc,class_name,simple_class,cause_type,format,map__37273,map__37273__$1,triage_data,phase,source,line,column,symbol,class$,cause,spec))
)
);
}finally {cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__orig_val__37281_37290;

cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__orig_val__37280_37289;
}
return cljs.core.str.cljs$core$IFn$_invoke$arity$1(sb__4661__auto__);
})());
} else {
return format.call(null,"Execution error%s at %s(%s).\n%s\n",cause_type,(cljs.core.truth_(symbol)?[cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)," "].join(''):""),loc,cause);
}

break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__37275__$1)].join('')));

}
});
cljs.repl.error__GT_str = (function cljs$repl$error__GT_str(error){
return cljs.repl.ex_str.call(null,cljs.repl.ex_triage.call(null,cljs.repl.Error__GT_map.call(null,error)));
});

//# sourceMappingURL=repl.js.map?rel=1642872180337
