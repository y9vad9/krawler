# BrawlStars Gateway Module

`brawlstars` is a server-side infrastructure feature module that provides access to the official Brawl Stars
API.  
It handles querying the API in a rate-limited manner, persists raw responses into its own database tables, and runs
scheduled refresh tasks for all players and clubs. Bounded contexts consume this module via a service interface and map
the raw DTOs into their own domain entities.

The module manages API throttling, persistence, and scheduled refresh internally. Only the minimal interface is exposed
to bounded contexts, keeping them isolated and modular. This module is server-only and does not include domain or
application layers.
