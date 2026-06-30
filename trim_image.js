const { execSync } = require('child_process');

try {
    console.log("Trimming the image using sharp-cli...");
    execSync('npx -y sharp-cli@latest -i app/src/main/res/drawable/ic_mandoob_logo.jpg -o app/src/main/res/drawable/ic_mandoob_logo.jpg trim', { stdio: 'inherit' });
    console.log("Image trimmed successfully!");
} catch (error) {
    console.error("Failed to trim image:", error.message);
}
