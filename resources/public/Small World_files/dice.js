// Compiled by ClojureScript 1.10.520 {}
goog.provide('clj_fuzzy.dice');
goog.require('cljs.core');
goog.require('clojure.string');
goog.require('clojure.set');
goog.require('clj_fuzzy.helpers');
clj_fuzzy.dice.letter_sets = (function clj_fuzzy$dice$letter_sets(n,string){
return cljs.core.set.call(null,clj_fuzzy.helpers.n_grams.call(null,n,clojure.string.upper_case.call(null,clojure.string.replace.call(null,string,/\s+/,""))));
});
/**
 * Compute the Dice coefficient between two [strings].
 */
clj_fuzzy.dice.coefficient = (function clj_fuzzy$dice$coefficient(var_args){
var args__4736__auto__ = [];
var len__4730__auto___29948 = arguments.length;
var i__4731__auto___29949 = (0);
while(true){
if((i__4731__auto___29949 < len__4730__auto___29948)){
args__4736__auto__.push((arguments[i__4731__auto___29949]));

var G__29950 = (i__4731__auto___29949 + (1));
i__4731__auto___29949 = G__29950;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((2) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((2)),(0),null)):null);
return clj_fuzzy.dice.coefficient.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__4737__auto__);
});

clj_fuzzy.dice.coefficient.cljs$core$IFn$_invoke$arity$variadic = (function (string1,string2,p__29945){
var map__29946 = p__29945;
var map__29946__$1 = (((((!((map__29946 == null))))?(((((map__29946.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__29946.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__29946):map__29946);
var n = cljs.core.get.call(null,map__29946__$1,new cljs.core.Keyword(null,"n","n",562130025),(2));
if(cljs.core._EQ_.call(null,string1,string2)){
return 1.0;
} else {
if((((cljs.core.count.call(null,string1) < (2))) && ((cljs.core.count.call(null,string2) < (2))))){
return 0.0;
} else {
var p1 = clj_fuzzy.dice.letter_sets.call(null,n,string1);
var p2 = clj_fuzzy.dice.letter_sets.call(null,n,string2);
var sum = (cljs.core.count.call(null,p1) + cljs.core.count.call(null,p2));
return ((2.0 * cljs.core.count.call(null,clojure.set.intersection.call(null,p1,p2))) / sum);

}
}
});

clj_fuzzy.dice.coefficient.cljs$lang$maxFixedArity = (2);

/** @this {Function} */
clj_fuzzy.dice.coefficient.cljs$lang$applyTo = (function (seq29942){
var G__29943 = cljs.core.first.call(null,seq29942);
var seq29942__$1 = cljs.core.next.call(null,seq29942);
var G__29944 = cljs.core.first.call(null,seq29942__$1);
var seq29942__$2 = cljs.core.next.call(null,seq29942__$1);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__29943,G__29944,seq29942__$2);
});


//# sourceMappingURL=dice.js.map?rel=1642872176934
