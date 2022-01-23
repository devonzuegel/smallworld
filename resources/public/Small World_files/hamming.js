// Compiled by ClojureScript 1.10.520 {}
goog.provide('clj_fuzzy.hamming');
goog.require('cljs.core');
/**
 * Compute the Hamming distance between [seq1] and [seq2].
 */
clj_fuzzy.hamming.distance = (function clj_fuzzy$hamming$distance(seq1,seq2){
var l1 = cljs.core.count.call(null,seq1);
var l2 = cljs.core.count.call(null,seq2);
if(cljs.core._EQ_.call(null,l1,l2)){
return cljs.core.count.call(null,cljs.core.remove.call(null,((function (l1,l2){
return (function (p1__29911_SHARP_){
return cljs.core._EQ_.call(null,cljs.core.first.call(null,p1__29911_SHARP_),cljs.core.second.call(null,p1__29911_SHARP_));
});})(l1,l2))
,cljs.core.partition.call(null,(2),cljs.core.interleave.call(null,seq1,seq2))));
} else {
return null;
}
});

//# sourceMappingURL=hamming.js.map?rel=1642872176910
