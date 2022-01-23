// Compiled by ClojureScript 1.10.520 {}
goog.provide('clj_fuzzy.match_rating');
goog.require('cljs.core');
goog.require('clojure.string');
goog.require('clj_fuzzy.helpers');
clj_fuzzy.match_rating.drop_non_leading_vowel = (function clj_fuzzy$match_rating$drop_non_leading_vowel(word){
return cljs.core.apply.call(null,cljs.core.str,cljs.core.first.call(null,word),clojure.string.replace.call(null,clj_fuzzy.helpers.eat.call(null,word),/[AEIOU]/,""));
});
clj_fuzzy.match_rating.prep_word = (function clj_fuzzy$match_rating$prep_word(word){
return clj_fuzzy.helpers.clean_non_alphabetical.call(null,clojure.string.upper_case.call(null,word));
});
clj_fuzzy.match_rating.drop_nil = (function clj_fuzzy$match_rating$drop_nil(sequence){
return cljs.core.remove.call(null,cljs.core.nil_QMARK_,sequence);
});
clj_fuzzy.match_rating.get_codex_letters = (function clj_fuzzy$match_rating$get_codex_letters(pword){
var last_3 = (function (){var x__4222__auto__ = (3);
var y__4223__auto__ = (cljs.core.count.call(null,pword) - (3));
return ((x__4222__auto__ < y__4223__auto__) ? x__4222__auto__ : y__4223__auto__);
})();
return cljs.core.apply.call(null,cljs.core.str,cljs.core.concat.call(null,cljs.core.take.call(null,(3),pword),cljs.core.take_last.call(null,last_3,pword)));
});
clj_fuzzy.match_rating.minimum_rating = (function clj_fuzzy$match_rating$minimum_rating(codex_1,codex_2){
var length = (cljs.core.count.call(null,codex_1) + cljs.core.count.call(null,codex_2));
if((length <= (4))){
return (5);
} else {
if((((length > (4))) && ((length <= (7))))){
return (4);
} else {
if((((length > (7))) && ((length <= (11))))){
return (3);
} else {
return (2);

}
}
}
});
clj_fuzzy.match_rating.compute_longest = (function clj_fuzzy$match_rating$compute_longest(codex_1,codex_2){
var count_1 = cljs.core.count.call(null,codex_1);
var count_2 = cljs.core.count.call(null,codex_2);
if((count_1 > count_2)){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [codex_1,codex_2], null);
} else {
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [codex_2,codex_1], null);
}
});
clj_fuzzy.match_rating.make_aggregate = (function clj_fuzzy$match_rating$make_aggregate(longest,shortest){
return cljs.core.partition.call(null,(2),cljs.core.interleave.call(null,longest,cljs.core.concat.call(null,shortest,cljs.core.repeat.call(null,(cljs.core.count.call(null,longest) - cljs.core.count.call(null,shortest)),null))));
});
clj_fuzzy.match_rating.reverse_aggregate = (function clj_fuzzy$match_rating$reverse_aggregate(aggregate){
var longest = clj_fuzzy.match_rating.drop_nil.call(null,cljs.core.map.call(null,cljs.core.first,aggregate));
var shortest = clj_fuzzy.match_rating.drop_nil.call(null,cljs.core.map.call(null,cljs.core.second,aggregate));
return clj_fuzzy.match_rating.make_aggregate.call(null,cljs.core.reverse.call(null,longest),cljs.core.reverse.call(null,shortest));
});
clj_fuzzy.match_rating.codex_comparison = (function clj_fuzzy$match_rating$codex_comparison(aggregate){
return cljs.core.filter.call(null,(function (p1__29905_SHARP_){
return cljs.core.not_EQ_.call(null,cljs.core.first.call(null,p1__29905_SHARP_),cljs.core.second.call(null,p1__29905_SHARP_));
}),aggregate);
});
clj_fuzzy.match_rating.unmatched_characters = (function clj_fuzzy$match_rating$unmatched_characters(similarity_sequence){
return cljs.core.count.call(null,clj_fuzzy.match_rating.drop_nil.call(null,similarity_sequence));
});
clj_fuzzy.match_rating.similarity_rating = (function clj_fuzzy$match_rating$similarity_rating(codex_1,codex_2){
var vec__29906 = clj_fuzzy.match_rating.compute_longest.call(null,codex_1,codex_2);
var longest = cljs.core.nth.call(null,vec__29906,(0),null);
var shortest = cljs.core.nth.call(null,vec__29906,(1),null);
var similarity_sequence = clj_fuzzy.match_rating.codex_comparison.call(null,clj_fuzzy.match_rating.reverse_aggregate.call(null,clj_fuzzy.match_rating.codex_comparison.call(null,clj_fuzzy.match_rating.make_aggregate.call(null,longest,shortest))));
return ((6) - clj_fuzzy.match_rating.unmatched_characters.call(null,similarity_sequence));
});
/**
 * Compute the MRA codex for a [word].
 */
clj_fuzzy.match_rating.mra_codex = (function clj_fuzzy$match_rating$mra_codex(word){
return clj_fuzzy.match_rating.get_codex_letters.call(null,clj_fuzzy.helpers.distinct_consecutive.call(null,clj_fuzzy.match_rating.drop_non_leading_vowel.call(null,clj_fuzzy.match_rating.prep_word.call(null,word))));
});
/**
 * Compare two [words] using the Match Rating Approach
 */
clj_fuzzy.match_rating.mra_comparison = (function clj_fuzzy$match_rating$mra_comparison(word_1,word_2){
var codex_1 = clj_fuzzy.match_rating.mra_codex.call(null,word_1);
var codex_2 = clj_fuzzy.match_rating.mra_codex.call(null,word_2);
var difference = Math.abs((cljs.core.count.call(null,codex_1) - cljs.core.count.call(null,codex_2)));
if((difference < (3))){
var minimum = clj_fuzzy.match_rating.minimum_rating.call(null,codex_1,codex_2);
var similarity = clj_fuzzy.match_rating.similarity_rating.call(null,codex_1,codex_2);
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"minimum","minimum",-1621006059),minimum,new cljs.core.Keyword(null,"similarity","similarity",1871163855),similarity,new cljs.core.Keyword(null,"codex","codex",-1831831581),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [codex_1,codex_2], null),new cljs.core.Keyword(null,"match","match",1220059550),(similarity >= minimum)], null);
} else {
return null;
}
});

//# sourceMappingURL=match_rating.js.map?rel=1642872176903
