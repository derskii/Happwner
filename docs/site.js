// A local illustration of the presets. No requests or subscription processing.
(() => {
  const switcher = document.querySelector('.profile-switcher');
  if (!switcher) return;

  const profiles = {
    apple: {
      model: 'iPhone 11',
      os: 'iOS 18.0',
      ua: 'Happ/4.10.1/ios',
      hwid: 'UUID',
    },
    android: {
      model: 'Google Pixel 8',
      os: 'Android 15',
      ua: 'Happ/4.10.1/Android',
      hwid: '16 hex-символов',
    },
    windows: {
      model: 'PC',
      os: 'Windows 11',
      ua: 'Happ/4.10.1/Windows',
      hwid: 'UUID',
    },
  };

  const fields = Object.fromEntries(
    ['model', 'os', 'ua', 'hwid'].map(key => [key, document.getElementById(`profile-${key}`)]),
  );
  if (Object.values(fields).some(field => !field)) return;

  switcher.addEventListener('click', event => {
    const button = event.target.closest('button[data-profile]');
    if (!button || !switcher.contains(button)) return;

    const profile = profiles[button.dataset.profile];
    if (!profile) return;

    switcher.querySelectorAll('button').forEach(item => {
      item.setAttribute('aria-pressed', String(item === button));
    });
    Object.entries(profile).forEach(([key, value]) => {
      fields[key].textContent = value;
    });
  });

  switcher.hidden = false;
})();
