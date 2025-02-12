╔═ bank ═╗
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
        <div class="navbar bg-base-100">
        <div class="navbar-start">
            <a
                href="/user"
                class="btn btn-ghost text-xl link link-hover"
            >
                <img src="/favicon/favicon.svg" class="h-6 w-6" />
                PayMyBuddy
            </a>
        </div>
        <div class="navbar-end">
            <ul class="menu menu-horizontal px-1">
                <li>
                    <form
                        method="GET"
                        action="/user/bank"
                    >
                        <button>
                            Bank
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="GET"
                        action="/user/buddy"
                    >
                        <button>
                            Buddies
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="GET"
                        action="/user/profil"
                    >
                        <button>
                            Profil
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="POST"
                        action="/auth/log-out"
                    ><input type="hidden" name="_csrf" value=""/>
                        <button>
                            Disconnect
                        </button>
                    </form>
                </li>
            </ul>
        </div>
    </div>
        <div
            class="flex h-screen justify-center items-center place-items-center"
        >
            <div class="card bg-base-100 min-w-80 shadow-xl">
                <div class="card-body">
                    <h2 class="card-title">Add or remove fund</h2>
                    
                    <div class="join p-4">
                        <div class="join join-item join-vertical px-4">
                            <h3 class="jox-item py-2">Username</h3>
                            <h3 class="join-item py-2">Current balance:</h3>
                        </div>
                        <div class="join join-item join-vertical px-4">
                            <h3 class="join-item py-2">
                                John Doe
                            </h3>
                            <h3 class="join-item pt-2">
                                ¤0.00
                            </h3>
                        </div>
                    </div>
                    <div class="card-actions justify-around p-4">
                        <form
                            method="post"
                            action="/user/bank/fromBankToApp"
                            class="flex flex-col items-center space-y-2 py-2"
                        ><input type="hidden" name="_csrf" value=""/>
                            <input
        type="number"
        name="change"
        placeholder="Amount"
        class="join-item input input-bordered" required="required"
    />
                            <button class="btn btn-info">
                                Add fund to account
                            </button>
                        </form>
                        <form
                            method="post"
                            action="/user/bank/fromAppToBank"
                            class="flex flex-col items-center space-y-2 py-2"
                        ><input type="hidden" name="_csrf" value=""/>
                            <input
        type="number"
        name="change"
        placeholder="Amount"
        class="join-item input input-bordered" required="required"
    />
                            <button class="btn btn-info">
                                Remove fund from account
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>

╔═ buddy ═╗
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
        <div class="navbar bg-base-100">
        <div class="navbar-start">
            <a
                href="/user"
                class="btn btn-ghost text-xl link link-hover"
            >
                <img src="/favicon/favicon.svg" class="h-6 w-6" />
                PayMyBuddy
            </a>
        </div>
        <div class="navbar-end">
            <ul class="menu menu-horizontal px-1">
                <li>
                    <form
                        method="GET"
                        action="/user/bank"
                    >
                        <button>
                            Bank
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="GET"
                        action="/user/buddy"
                    >
                        <button>
                            Buddies
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="GET"
                        action="/user/profil"
                    >
                        <button>
                            Profil
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="POST"
                        action="/auth/log-out"
                    ><input type="hidden" name="_csrf" value=""/>
                        <button>
                            Disconnect
                        </button>
                    </form>
                </li>
            </ul>
        </div>
    </div>
        <div
            class="flex h-screen justify-center items-center place-items-center"
        >
            <div class="card bg-base-100 shadow-xl">
                <div class="card-body">
                    <h2 class="card-title">Add a buddy</h2>
                    
                    <form method="post" action="/user/buddy" class="space-y-2"><input type="hidden" name="_csrf" value=""/>
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
                        <div class="card-actions p-4 justify-center">
                            <button class="btn btn-warning">
                                Add buddy
                            </button>
                        </div>
                    </form>
                </div>
            </div>
    </body>
</html>

╔═ profil ═╗
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
        <div class="navbar bg-base-100">
        <div class="navbar-start">
            <a
                href="/user"
                class="btn btn-ghost text-xl link link-hover"
            >
                <img src="/favicon/favicon.svg" class="h-6 w-6" />
                PayMyBuddy
            </a>
        </div>
        <div class="navbar-end">
            <ul class="menu menu-horizontal px-1">
                <li>
                    <form
                        method="GET"
                        action="/user/bank"
                    >
                        <button>
                            Bank
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="GET"
                        action="/user/buddy"
                    >
                        <button>
                            Buddies
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="GET"
                        action="/user/profil"
                    >
                        <button>
                            Profil
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="POST"
                        action="/auth/log-out"
                    ><input type="hidden" name="_csrf" value=""/>
                        <button>
                            Disconnect
                        </button>
                    </form>
                </li>
            </ul>
        </div>
    </div>
        <div
            class="flex h-screen justify-center items-center place-items-center"
        >
            <div class="card bg-base-100 min-w-80 shadow-xl">
                <div class="card-body">
                    <h2 class="card-title">Profil</h2>
                    <div class="join p-4">
                        <div class="join join-item join-vertical px-4">
                            <h3 class="jox-item py-2">Id</h3>
                            <h3 class="jox-item py-2">Username</h3>
                            <h3 class="join-item py-2">Email</h3>
                            <h3 class="join-item py-2">Password</h3>
                            <h3 class="join-item py-2">Role</h3>
                            <h3 class="join-item py-2">Balance</h3>
                        </div>
                        <div class="join join-item join-vertical px-4">
                            <h3 class="join-item py-2">
                                01234567-abcd-1abc-abcd-0123456789ab
                            </h3>
                            <h3 class="join-item py-2">
                                John Doe
                            </h3>
                            <h3 class="join-item py-2">
                                john.doe@test.com
                            </h3>
                            <h3 class="join-item py-2">
                                
                            </h3>
                            <h3 class="join-item py-2">
                                USER
                            </h3>
                            <h3 class="join-item pt-2">
                                ¤0.00
                            </h3>
                        </div>
                    </div>
                    <div class="card-actions justify-around p-4">
                        <a
                            href="/user/profil/update"
                            role="button"
                            class="btn btn-warning"
                        >Modify</a>
                        <button
                            class="btn btn-error"
                            onclick="deleteModal.showModal()"
                        >
                            Delete
                        </button>
                        <dialog id="deleteModal" class="modal">
                            <div
                                class="modal-box flex flex-col items-center w-6/12 max-w-5xl"
                            >
                                <form method="dialog">
                                    <button
                                        class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2"
                                    >
                                        ✕
                                    </button>
                                </form>
                                <h3 class="text-lg font-bold self-start">
                                    Confirm user deletion
                                </h3>
                                <p class="py-4">
                                    Are you sure you want to delete your user
                                    'john.doe@test.com' ?
                                </p>
                                <div class="modal-action m-0">
                                    <form
                                        method="post"
                                        action="/user/profil/update"
                                        class="flex-1"
                                    ><input type="hidden" name="_csrf" value=""/>
                                        <button class="btn btn-error">
                                            DELETE USER
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </dialog>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>

╔═ profilForm ═╗
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
        <div class="navbar bg-base-100">
        <div class="navbar-start">
            <a
                href="/user"
                class="btn btn-ghost text-xl link link-hover"
            >
                <img src="/favicon/favicon.svg" class="h-6 w-6" />
                PayMyBuddy
            </a>
        </div>
        <div class="navbar-end">
            <ul class="menu menu-horizontal px-1">
                <li>
                    <form
                        method="GET"
                        action="/user/bank"
                    >
                        <button>
                            Bank
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="GET"
                        action="/user/buddy"
                    >
                        <button>
                            Buddies
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="GET"
                        action="/user/profil"
                    >
                        <button>
                            Profil
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="POST"
                        action="/auth/log-out"
                    ><input type="hidden" name="_csrf" value=""/>
                        <button>
                            Disconnect
                        </button>
                    </form>
                </li>
            </ul>
        </div>
    </div>
        <form
            method="post"
            action="/user/profil/update"
            class="flex-1"
        ><input type="hidden" name="_csrf" value=""/>
            <div
                class="flex h-screen justify-center items-center place-items-center"
            >
                <div class="card bg-base-100 shadow-xl">
                    <h2 class="card-title">Edit profil</h2>
                    
                    <div class="join p-4 items-center">
                        <div class="join join-item join-vertical px-4">
                            <h3 class="join-item py-5">Username</h3>
                            <h3 class="join-item py-5">Email</h3>
                            <h3 class="join-item py-5">Password</h3>
                        </div>
                        <div class="join join-item join-vertical px-4">
                            <h3 class="join-item py-2">
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
            placeholder="John Doe"
            class="grow"
        />
    </label>
                            </h3>
                            <h3 class="join-item py-2">
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
            placeholder="john.doe@test.com"
            class="grow"
        />
    </label>
                            </h3>
                            <h3 class="join-item py-2">
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
            placeholder="
            class="grow"
        />
    </label>
                            </h3>
                        </div>
                    </div>
                    <div class="card-actions justify-end p-4">
                        <button class="btn btn-info">
                            Update
                        </button>
                    </div>
                </div>
            </div>
        </form>
    </body>
</html>

╔═ user ═╗
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
    <link href="css/sortable.min.css" rel="stylesheet" />
    <script src="js/sortable.min.js"></script>
    <script>
            window.addEventListener("load", function () {
                const sortTransaction = document.getElementById(
                    "transactionDate",
                );
                const sortBankTransaction = document
                    .getElementById("bankTransactionDate");

                if (sortTransaction) {
                    sortTransaction.click();
                }
                if (sortBankTransaction) {
                    sortBankTransaction.click();
                }
            });
        </script>
  </head>
  <body id="root" class="flex-1 flex-col">
        <div class="navbar bg-base-100">
        <div class="navbar-start">
            <a
                href="/user"
                class="btn btn-ghost text-xl link link-hover"
            >
                <img src="/favicon/favicon.svg" class="h-6 w-6" />
                PayMyBuddy
            </a>
        </div>
        <div class="navbar-end">
            <ul class="menu menu-horizontal px-1">
                <li>
                    <form
                        method="GET"
                        action="/user/bank"
                    >
                        <button>
                            Bank
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="GET"
                        action="/user/buddy"
                    >
                        <button>
                            Buddies
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="GET"
                        action="/user/profil"
                    >
                        <button>
                            Profil
                        </button>
                    </form>
                </li>
                <li>
                    <form
                        method="POST"
                        action="/auth/log-out"
                    ><input type="hidden" name="_csrf" value=""/>
                        <button>
                            Disconnect
                        </button>
                    </form>
                </li>
            </ul>
        </div>
    </div>
        <div class="flex justify-center items-center place-items-center w-full">
            <div class="card bg-base-100 w-3/4">
                <div class="card-body">
                    <h2 class="card-title">Profil</h2>
                    
                    <form
        action="/user/transaction"
        method="post"
        class="flex"
    ><input type="hidden" name="_csrf" value=""/>
        <div class="flex-1 join justify-around">
            <details class="join-item dropdown">
                <summary class="btn">
                    Select a buddy
                </summary>
                <ul
                    class="menu dropdown-content bg-base-100 rounded-box w-40 shadow"
                >
                    
                </ul>
            </details>
            <input
                type="text"
                name="description"
                placeholder="Description"
                class="join-item input input-bordered"
            />

            <input
        type="number"
        name="change"
        placeholder="Amount"
        class="join-item input input-bordered" required="required"
    />
            <div class="join-item justify-around">
                <button class="btn btn-info">Pay</button>
            </div>
        </div>
    </form>
                </div>
            </div>
        </div>
        <div class="flex justify-around p-24">
            <div
        class="indicator"
    >
        <span
            class="indicator-item indicator-top indicator-start badge badge-primary"
        >
            My transactions
        </span>
        <div class="place-items-center space-y-4">
            <table class="table table-zebra sortable">
                <thead>
                    <tr>
                        <th id="transactionDate">Date</th>
                        <th>Buddy</th>
                        <th>Description</th>
                        <th>Amount</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>
                            January 1, 1970, 12:00:00 AM
                        </td>
                        <td>Jane Doe</td>
                        <td>description</td>
                        <td>
                            -¤13.37
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
            <div
        class="indicator"
    >
        <span
            class="indicator-item indicator-top indicator-start badge badge-primary"
        >
            My bank transactions
        </span>
        <div class="place-items-center space-y-4">
            <table class="table table-zebra sortable">
                <thead>
                    <tr>
                        <th id="bankTransactionDate">Date</th>
                        <th>Amount</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>
                            January 1, 1970, 12:00:00 AM
                        </td>
                        <td>
                            ¤13.37
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
        </div>
    </body>
</html>

╔═ [end of file] ═╗
