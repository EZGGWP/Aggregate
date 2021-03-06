PGDMP                         y         	   agregator    13.2    13.2     ?           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            ?           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            ?           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            ?           1262    16394 	   agregator    DATABASE     m   CREATE DATABASE agregator WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'English_United States.1252';
    DROP DATABASE agregator;
                postgres    false            y           1247    16438 	   enum_stat    TYPE     K   CREATE TYPE public.enum_stat AS ENUM (
    'sent',
    'acc',
    'dec'
);
    DROP TYPE public.enum_stat;
       public          postgres    false            ?            1259    16419    friends    TABLE     s   CREATE TABLE public.friends (
    "sId" integer NOT NULL,
    "rId" integer NOT NULL,
    stat boolean NOT NULL
);
    DROP TABLE public.friends;
       public         heap    postgres    false            ?            1259    16397    users    TABLE     D  CREATE TABLE public.users (
    id integer NOT NULL,
    username text NOT NULL,
    password text NOT NULL,
    "regDate" date NOT NULL,
    achievements json DEFAULT '{    "achievements":{       "Steam":[                 ],       "GitHub":[                 ],       "Spotify":[                 ]    } }'::json NOT NULL
);
    DROP TABLE public.users;
       public         heap    postgres    false            ?            1259    16395    users_id_seq    SEQUENCE     ?   CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.users_id_seq;
       public          postgres    false    201            ?           0    0    users_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;
          public          postgres    false    200            *           2604    16400    users id    DEFAULT     d   ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);
 7   ALTER TABLE public.users ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    200    201    201            -           2606    16405    users users_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
       public            postgres    false    201            /           2606    24586    friends receiver    FK CONSTRAINT     w   ALTER TABLE ONLY public.friends
    ADD CONSTRAINT receiver FOREIGN KEY ("rId") REFERENCES public.users(id) NOT VALID;
 :   ALTER TABLE ONLY public.friends DROP CONSTRAINT receiver;
       public          postgres    false    201    2861    202            .           2606    24576    friends sender    FK CONSTRAINT     u   ALTER TABLE ONLY public.friends
    ADD CONSTRAINT sender FOREIGN KEY ("sId") REFERENCES public.users(id) NOT VALID;
 8   ALTER TABLE ONLY public.friends DROP CONSTRAINT sender;
       public          postgres    false    201    2861    202           