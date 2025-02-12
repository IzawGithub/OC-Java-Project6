╔═ auth ═╗
<!DOCTYPE html>
<html
  class="h-dvh"
  data-theme="light" xmlns="http://www.w3.org/1999/xhtml"
>
  <head>
    <meta charset="UTF-8" />
    <link
      href="/favicon/favicon.svg"
      rel="icon"
    />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    
    <title>PayMyBuddy</title>
    <!-- TODO: Use an actual package manager instead of downloaded CDN file -->
    <link
      href="/css/daisyui.min.css"
      rel="stylesheet"
      type="text/css"
    />
    <script src="/js/tailwind.min.js"></script>
  </head>
  <body id="root" class="flex-1 flex-col">
    <div
      class="flex flex-1 h-screen justify-center items-center place-items-center"
    >
      <div class="card bg-base-100 w-96 place-items-center shadow-xl">
        <div class="card-body">
          <h2
        class="card-title bg-warning rounded flex items-center justify-center"
    >
        PayMyBuddy
    </h2>
          <div class="flex w-full">
            <div
              class="card rounded-box grid h-20 flex-grow place-items-center"
            >
              <a
                href="/auth/log-in"
                role="button"
                class="btn btn-info"
              >
                Log-in
              </a>
            </div>
            <div class="divider divider-horizontal"></div>
            <div
              class="card rounded-box grid h-20 flex-grow place-items-center"
            >
              <a
                href="/auth/sign-up"
                role="button"
                class="btn btn-info"
              >
                Sign-up
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>

╔═ getLogin ═╗
<!DOCTYPE html>
<html
  class="h-dvh"
  data-theme="light" xmlns="http://www.w3.org/1999/xhtml"
>
  <head>
    <meta charset="UTF-8" />
    <link
      href="/favicon/favicon.svg"
      rel="icon"
    />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    
    <title>PayMyBuddy</title>
    <!-- TODO: Use an actual package manager instead of downloaded CDN file -->
    <link
      href="/css/daisyui.min.css"
      rel="stylesheet"
      type="text/css"
    />
    <script src="/js/tailwind.min.js"></script>
  </head>
  <body id="root" class="flex-1">
        <div
            class="flex h-screen justify-center items-center place-items-center"
        >
            <div class="card bg-base-100 w-96 place-items-center shadow-xl">
                <div class="card-body">
                    <h2
        class="card-title bg-warning rounded flex items-center justify-center"
    >
        PayMyBuddy
    </h2>

                    
                    
                    

                    <form method="post" action="/auth/log-in" class="space-y-2"><input type="hidden" name="_csrf" value=""/>
        <label
        class="input input-bordered flex items-center gap-2"
    >
        <svg
        xmlns="http://www.w3.org/2000/svg"
        fill="currentColor"
        viewBox="0 0 16 16"
        class="h-4 w-4 opacity-70"
    >
        <path
            d="M2.5 3A1.5 1.5 0 0 0 1 4.5v.793c.026.009.051.02.076.032L7.674 8.51c.206.1.446.1.652 0l6.598-3.185A.755.755 0 0 1 15 5.293V4.5A1.5 1.5 0 0 0 13.5 3h-11Z"
        />
        <path
            d="M15 6.954 8.978 9.86a2.25 2.25 0 0 1-1.956 0L1 6.954V11.5A1.5 1.5 0 0 0 2.5 13h11a1.5 1.5 0 0 0 1.5-1.5V6.954Z"
        />
    </svg>
        <input
            name="email"
            type="text"
            placeholder="Email"
            class="grow" required="required"
        />
    </label>
        <label
        class="input input-bordered flex items-center gap-2"
    >
        <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 16 16"
        fill="currentColor"
        class="h-4 w-4 opacity-70"
    >
        <path
            clip-rule="evenodd"
            fill-rule="evenodd"
            d="M14 6a4 4 0 0 1-4.899 3.899l-1.955 1.955a.5.5 0 0 1-.353.146H5v1.5a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1-.5-.5v-2.293a.5.5 0 0 1 .146-.353l3.955-3.955A4 4 0 1 1 14 6Zm-4-2a.75.75 0 0 0 0 1.5.5.5 0 0 1 .5.5.75.75 0 0 0 1.5 0 2 2 0 0 0-2-2Z"
        />
    </svg>
        <input
            name="password"
            type="password"
            placeholder="Password"
            class="grow" required="required"
        />
    </label>
        
        <label
        class="cursor-pointer label"
    >
        <span class="label-text">Stay logged in</span>
        <input
            name="remember-me"
            type="checkbox"
            class="checkbox checkbox-info"
        />
    </label>

        <div class="card-actions flex-1 flex-col items-center space-y-1 py-1">
            <button class="btn btn-info">
                Log in
            </button>
            <a href="/auth/sign-up" class="prose">No account? Sign-up</a>
        </div>
    </form>
                </div>
            </div>
        </div>
    </body>
</html>

╔═ getSignUp ═╗
<!DOCTYPE html>
<html
  class="h-dvh"
  data-theme="light" xmlns="http://www.w3.org/1999/xhtml"
>
  <head>
    <meta charset="UTF-8" />
    <link
      href="/favicon/favicon.svg"
      rel="icon"
    />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    
    <title>PayMyBuddy</title>
    <!-- TODO: Use an actual package manager instead of downloaded CDN file -->
    <link
      href="/css/daisyui.min.css"
      rel="stylesheet"
      type="text/css"
    />
    <script src="/js/tailwind.min.js"></script>
  </head>
  <body id="root" class="flex-1">
        <div
            class="flex h-screen justify-center items-center place-items-center"
        >
            <div class="card bg-base-100 w-96 place-items-center shadow-xl">
                <div class="card-body">
                    <h2
        class="card-title bg-warning rounded flex items-center justify-center"
    >
        PayMyBuddy
    </h2>

                    
                    
                    

                    <form method="post" action="/auth/sign-up" class="space-y-2"><input type="hidden" name="_csrf" value=""/>
        <label
        class="input input-bordered flex items-center gap-2"
    >
        <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 16 16"
        fill="currentColor"
        class="h-4 w-4 opacity-70"
    >
        <path
            d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6ZM12.735 14c.618 0 1.093-.561.872-1.139a6.002 6.002 0 0 0-11.215 0c-.22.578.254 1.139.872 1.139h9.47Z"
        />
    </svg>
        <input
            name="username"
            type="text"
            placeholder="Username"
            class="grow" required="required"
        />
    </label>

        <label
        class="input input-bordered flex items-center gap-2"
    >
        <svg
        xmlns="http://www.w3.org/2000/svg"
        fill="currentColor"
        viewBox="0 0 16 16"
        class="h-4 w-4 opacity-70"
    >
        <path
            d="M2.5 3A1.5 1.5 0 0 0 1 4.5v.793c.026.009.051.02.076.032L7.674 8.51c.206.1.446.1.652 0l6.598-3.185A.755.755 0 0 1 15 5.293V4.5A1.5 1.5 0 0 0 13.5 3h-11Z"
        />
        <path
            d="M15 6.954 8.978 9.86a2.25 2.25 0 0 1-1.956 0L1 6.954V11.5A1.5 1.5 0 0 0 2.5 13h11a1.5 1.5 0 0 0 1.5-1.5V6.954Z"
        />
    </svg>
        <input
            name="email"
            type="text"
            placeholder="Email"
            class="grow" required="required"
        />
    </label>
        <label
        class="input input-bordered flex items-center gap-2"
    >
        <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 16 16"
        fill="currentColor"
        class="h-4 w-4 opacity-70"
    >
        <path
            clip-rule="evenodd"
            fill-rule="evenodd"
            d="M14 6a4 4 0 0 1-4.899 3.899l-1.955 1.955a.5.5 0 0 1-.353.146H5v1.5a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1-.5-.5v-2.293a.5.5 0 0 1 .146-.353l3.955-3.955A4 4 0 1 1 14 6Zm-4-2a.75.75 0 0 0 0 1.5.5.5 0 0 1 .5.5.75.75 0 0 0 1.5 0 2 2 0 0 0-2-2Z"
        />
    </svg>
        <input
            name="password"
            type="password"
            placeholder="Password"
            class="grow" required="required"
        />
    </label>
        

        <div class="card-actions flex-col items-center space-y-1 py-1">
            <button class="btn btn-info">
                Sign up
            </button>
            <a href="/auth/log-in" class="prose"
            >Already have an account? Log-in</a>
        </div>
    </form>
                </div>
            </div>
        </div>
    </body>
</html>

╔═ [end of file] ═╗
