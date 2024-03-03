const exec = require('child_process').exec;

const apps = [
  {
    name: 'agreement-management',
    baseHref: '/agreement/'
  },
  {
    name: 'party-management',
    baseHref: '/party/'
  },
  {
    name: 'resource-catalog-management',
    baseHref: '/catalog/'
  },
  {
    name: 'resource-order-management',
    baseHref: '/order/'
  },
  {
    name: 'reporting-system',
    baseHref: '/reporting/'
  }
];

const buildModeArgIndex = process.argv.findIndex(arg => arg === '--buildMode');
let buildMode = 'development';

if (buildModeArgIndex !== -1 && process.argv[buildModeArgIndex + 1]) {
  buildMode = process.argv[buildModeArgIndex + 1];
}

function buildApp(app) {
  return new Promise((resolve, reject) => {
    const childProcess = exec(`nx run ${app.name}:build:${buildMode} --baseHref=${app.baseHref} --verbose`, (error, stdout, stderr) => {
      if (error) {
        console.warn(`Error while building ${app.name}: ${error}`);
        return reject(error);
      }
      console.log("-------------------------------------------")
      console.log(`App ${app.name} successfully built!`);
      console.log("-------------------------------------------")
      resolve();
    });

    // Forward stdout+stderr to this process
    childProcess.stdout.pipe(process.stdout);
    childProcess.stderr.pipe(process.stderr);
  });
}

async function buildAllApps() {
  for (const app of apps) {
    await buildApp(app);
  }
}

buildAllApps().then(() => {
  console.log("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-")
  console.log('All apps successfully built!');
  console.log("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-")
});
// ~adlatus-gui: docker build -t adlatus-gui -f ci/Dockerfile .
