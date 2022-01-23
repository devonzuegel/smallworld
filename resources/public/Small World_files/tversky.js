// Compiled by ClojureScript 1.10.520 {}
goog.provide('clj_fuzzy.tversky');
goog.require('cljs.core');
goog.require('clojure.set');
clj_fuzzy.tversky.I = (function clj_fuzzy$tversky$I(X,Y){
return cljs.core.count.call(null,clojure.set.intersection.call(null,X,Y));
});
clj_fuzzy.tversky.R = (function clj_fuzzy$tversky$R(X,Y){
return cljs.core.count.call(null,clojure.set.difference.call(null,X,Y));
});
/**
 * Compute the original assymmetric Tversky index for the given [sequences] and
 * the given [alpha] and [beta].
 */
clj_fuzzy.tversky.assymmetric_index = (function clj_fuzzy$tversky$assymmetric_index(seq1,seq2,alpha,beta){
var X = cljs.core.set.call(null,seq1);
var Y = cljs.core.set.call(null,seq2);
var XIY = clj_fuzzy.tversky.I.call(null,X,Y);
return (XIY / ((XIY + (alpha * clj_fuzzy.tversky.R.call(null,X,Y))) + (beta * clj_fuzzy.tversky.R.call(null,Y,X))));
});
/**
 * Compute the symmetric variant of the Tversky undex for the given [sequences] and
 * the given [alpha] and [beta].
 */
clj_fuzzy.tversky.symmetric_index = (function clj_fuzzy$tversky$symmetric_index(seq1,seq2,alpha,beta){
var X = cljs.core.set.call(null,seq1);
var Y = cljs.core.set.call(null,seq2);
var XIY = clj_fuzzy.tversky.I.call(null,X,Y);
var X_Y = clj_fuzzy.tversky.R.call(null,X,Y);
var Y_X = clj_fuzzy.tversky.R.call(null,Y,X);
var a = (function (){var x__4222__auto__ = X_Y;
var y__4223__auto__ = Y_X;
return ((x__4222__auto__ < y__4223__auto__) ? x__4222__auto__ : y__4223__auto__);
})();
var b = (function (){var x__4219__auto__ = X_Y;
var y__4220__auto__ = Y_X;
return ((x__4219__auto__ > y__4220__auto__) ? x__4219__auto__ : y__4220__auto__);
})();
return (XIY / (XIY + (beta * ((alpha * a) + Math.pow(((1) - alpha),b)))));
});
/**
 * Compute the tversky index for the given [sequences] with given [:alpha] and [:beta]
 * and in a [:symmetric] fashion or not.
 */
clj_fuzzy.tversky.index = (function clj_fuzzy$tversky$index(var_args){
var args__4736__auto__ = [];
var len__4730__auto___29730 = arguments.length;
var i__4731__auto___29731 = (0);
while(true){
if((i__4731__auto___29731 < len__4730__auto___29730)){
args__4736__auto__.push((arguments[i__4731__auto___29731]));

var G__29732 = (i__4731__auto___29731 + (1));
i__4731__auto___29731 = G__29732;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((2) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((2)),(0),null)):null);
return clj_fuzzy.tversky.index.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__4737__auto__);
});

clj_fuzzy.tversky.index.cljs$core$IFn$_invoke$arity$variadic = (function (seq1,seq2,p__29727){
var map__29728 = p__29727;
var map__29728__$1 = (((((!((map__29728 == null))))?(((((map__29728.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__29728.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__29728):map__29728);
var alpha = cljs.core.get.call(null,map__29728__$1,new cljs.core.Keyword(null,"alpha","alpha",-1574982441),(1));
var beta = cljs.core.get.call(null,map__29728__$1,new cljs.core.Keyword(null,"beta","beta",455605892),(1));
var symmetric = cljs.core.get.call(null,map__29728__$1,new cljs.core.Keyword(null,"symmetric","symmetric",-960442568),false);
return (cljs.core.truth_(symmetric)?clj_fuzzy.tversky.symmetric_index:clj_fuzzy.tversky.assymmetric_index).call(null,seq1,seq2,alpha,beta);
});

clj_fuzzy.tversky.index.cljs$lang$maxFixedArity = (2);

/** @this {Function} */
clj_fuzzy.tversky.index.cljs$lang$applyTo = (function (seq29724){
var G__29725 = cljs.core.first.call(null,seq29724);
var seq29724__$1 = cljs.core.next.call(null,seq29724);
var G__29726 = cljs.core.first.call(null,seq29724__$1);
var seq29724__$2 = cljs.core.next.call(null,seq29724__$1);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__29725,G__29726,seq29724__$2);
});


//# sourceMappingURL=tversky.js.map?rel=1642872176658
