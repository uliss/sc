(
NodeJS.restart;
j = NodeJS_Slideshow.new;
j.autoAdd(true);
)

j.add
j.addImages(pathMatch("/Users/serj/Pictures/*jpg")[..4], 1536)
j.imageUrls
j.clear
j.addImagePattern("/Users/serj/Pictures/*jpg")
j.imageUrls
j.clear
j.addUrlPattern("*.jpg")
j.imageUrls

j.currentUrl
j.sync
j.next
j.prev
j.first
j.last

(
j.add;
j.sync
)
