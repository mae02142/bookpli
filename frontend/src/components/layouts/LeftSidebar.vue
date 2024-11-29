<template>
    <div class="sidebar">
        <img class="bookshelf-icon" src="@/assets/sidebar/bookshelf.png">
        <img class="playlist-icon" src="@/assets/sidebar/playlist.png">
    </div>
</template>

<style scoped>
.sidebar {
  width: 45px;
  background: #fffdf1;
  padding: 10px;
  display: flex;
  justify-items: center;
  flex-flow: column;
  gap: 25px;
}

.bookshelf-icon:hover {
    cursor: pointer;
}

.playlist-icon:hover {
    cursor: pointer;
}

</style>