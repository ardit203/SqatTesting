export function toggleCard(show){
    const box = document.getElementById("cardFields");
    if(box == null) return;
    box.style.display = show ? "block" : "none";
}
