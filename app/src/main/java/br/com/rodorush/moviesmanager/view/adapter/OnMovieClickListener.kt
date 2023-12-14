package br.com.rodorush.moviesmanager.view.adapter

interface OnMovieClickListener {
    fun onMovieClick(position: Int)
    fun onRemoveMovieMenuItemClick(position: Int)
    fun onEditMovieMenuItemClick(position: Int)
    fun onWatchedCheckBoxClick(position: Int, checked: Boolean)
//    fun onRatingBarClick(position: Int, rating: Float)
}