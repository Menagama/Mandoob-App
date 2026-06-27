const fs = require('fs');
['mdpi', 'hdpi', 'xhdpi', 'xxhdpi', 'xxxhdpi'].forEach(dpi => {
  const path = `app/src/main/res/mipmap-${dpi}/ic_launcher.png`;
  try {
    console.log(`${dpi}: ${fs.statSync(path).size}`);
  } catch(e) {
    console.log(`${dpi}: not found`);
  }
});
